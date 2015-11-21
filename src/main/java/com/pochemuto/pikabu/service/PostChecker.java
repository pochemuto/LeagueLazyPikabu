package com.pochemuto.pikabu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pochemuto.pikabu.dao.Post;
import com.pochemuto.pikabu.dao.PostRepository;
import com.pochemuto.pikabu.response.PageResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 17.11.2015
 */
@Service
public class PostChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostChecker.class);

    @Value("${pikabu.check.pagesUrl}")
    private String pageUrl;

    @Value("${pikabu.request.delayMsec}")
    private int requestIntervalMsec;

    @Value("${pikabu.check.maxPages}")
    private int maxPages;

    @Autowired
    private PageParser pageParser;

    @Autowired
    private WebClient client;

    @Autowired
    private PostRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private CommentService commentService;


    public void check() throws Exception {
        List<Post> newPosts = new ArrayList<>();
        for (int page = 0; page < maxPages; page++) {
            String url = String.format(pageUrl, page);
            LOGGER.debug("Fetching data from {}", url);
            Connection connection = client.json(url);
            PageResponse response = jsonMapper.readValue(connection.execute().body(), PageResponse.class);
            List<Post> pagePosts = pageParser.content(Jsoup.parse(response.getHtml()));
            if (pagePosts.size() != response.getPostIds().size()) {
                LOGGER.warn("Parsed posts count is not equal json posts count. Json = {}, parsed = {}",
                    response.getPostIds().size(), pagePosts.size());
            }

            List<Long> postIds = pagePosts.stream().map(Post::getId).collect(Collectors.toList());
            Set<Post> saved = new HashSet<>(postIds.size());
            repository.findByIdIn(postIds).stream().forEach(saved::add);
            pagePosts.stream().filter(t -> !saved.contains(t)).forEach(newPosts::add);

            Thread.sleep(requestIntervalMsec);
        }

        commentService.addComment(3795034, "Похоже на игру слов. Но при чем тут таз...");

        LOGGER.info("Saving {} posts", newPosts.size());
        for (Post post : newPosts) {
            commentService.addComment(post);
        }
        repository.save(newPosts);
    }
}
