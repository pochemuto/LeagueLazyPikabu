package com.pochemuto.pikabu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pochemuto.pikabu.dao.Post;
import com.pochemuto.pikabu.dao.PostRepository;
import com.pochemuto.pikabu.response.ResultResponse;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 21.11.2015
 */
@Service
public class CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private PostRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Value("${pikabu.comment.url}")
    private String commentUrl;

    private BlockingDeque<Post> queue = new LinkedBlockingDeque<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean shutdown = false;

    public void addComment(Post post) {
        queue.offer(post);
    }

    @PostConstruct
    private void init() {
        executorService.submit((Runnable) () -> {
            try {
                Post post;
                while ((post = queue.poll(5, TimeUnit.SECONDS)) != null && !shutdown) {
                    try {
                        sendComment(post);
                    } catch (IOException e) {
                        LOGGER.error("Error when sending comment to " + post.getId(), e);
                        queue.offer(post);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("interrupted", e);
            }
            LOGGER.info("sending comment thread finished");
        });
    }

    private void sendComment(Post post) throws IOException {


        //repository.saveAndFlush(post);
    }

    public boolean addComment(long postId, String message) throws IOException {
        Connection.Response response = webClient.connect(commentUrl).method(Connection.Method.POST)
            .data("action", "create")
            .data("story_id", String.valueOf(postId))
            .data("desc", message)
            .data("images", "[]")
            .data("parent_id", String.valueOf(0)).ignoreHttpErrors(true).execute();

        ResultResponse result = jsonMapper.readValue(response.body(), ResultResponse.class);
        LOGGER.info(response.statusMessage());
        LOGGER.info(response.body());
        if (result.isOk()) {
            LOGGER.info("Comment '{}' added to {}", message, postId);
        }
        return result.isOk();
    }

    private String wrapMessage(String message) {
        return "<p>" + message + "<br></p>";
    }


    @PreDestroy
    public void shutdown() throws InterruptedException {
        executorService.shutdownNow();
        shutdown = true;
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        LOGGER.warn("Not commented: " + queue.stream().map(Post::toString).collect(Collectors.joining(", ")));
    }
}
