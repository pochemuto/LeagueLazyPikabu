package com.pochemuto.pikabu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pochemuto.pikabu.dao.CommentTemplates;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;

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

    private ExecutorService executorService = Executors.newSingleThreadExecutor(r -> new Thread(r, "comment-sender"));

    private CommentTemplates templates;

    private volatile boolean shutdown = false;

    public void addComment(Post post) {
        queue.offer(post);
    }

    @PostConstruct
    private void init() throws JAXBException {
        templates = (CommentTemplates) JAXBContext.newInstance(CommentTemplates.class).createUnmarshaller()
            .unmarshal(getClass().getResourceAsStream("/comments.xml"));

        LOGGER.info("Loaded {} comment templates", templates.size());
        executorService.submit((Runnable) () -> {
            try {
                Post post;
                while ((post = queue.poll(5, TimeUnit.SECONDS)) != null || !shutdown) {
                    if (post == null) {
                        continue;
                    }
                    try {
                        sendComment(post);
                    } catch (IOException e) {
                        LOGGER.error("Error when sending comment to " + post, e);
                        queue.offer(post);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("interrupted", e);
            } catch (Throwable t) {
                LOGGER.error("error in commenting thread", t);
            }
            LOGGER.info("sending comment thread finished");
        });
    }

    private void sendComment(Post post) throws IOException {
        LOGGER.info("Sending comment in {}", post.getId());

        String message = templates.getRandom().replace("{}", post.getUrls().stream().collect(joining(", ")));

        if (addComment(post.getId(), wrapMessage(message))) {
            post.setCommented(true);
            repository.saveAndFlush(post);
        }
    }

    public boolean addComment(long postId, String message) throws IOException {
        Connection.Response response = webClient.ajax(commentUrl).method(Connection.Method.POST)
            .data("action", "create")
            .data("story_id", String.valueOf(postId))
            .data("desc", message)
            .data("images", "[]")
            .data("parent_id", String.valueOf(0)).ignoreHttpErrors(true).execute();

        ResultResponse result = jsonMapper.readValue(response.body(), ResultResponse.class);
        if (result.isOk()) {
            LOGGER.info("Comment '{}' added to {}", message, postId);
        } else {
            LOGGER.error("Comment '{}' wasn't added: {}", message, response.body());
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
        LOGGER.warn("Not commented: " + queue.stream().map(Post::toString).collect(joining(", ")));
    }
}
