package com.pochemuto.pikabu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pochemuto.pikabu.dao.PikabuThread;
import com.pochemuto.pikabu.dao.PikabuThreadRepository;
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
public class NewThreadChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewThreadChecker.class);

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
    private PikabuThreadRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private CommentService commentService;

    public void check() throws Exception {
        List<PikabuThread> newThreads = new ArrayList<>();
        for (int page = 0; page < maxPages; page++) {
            String url = String.format(pageUrl, page);
            LOGGER.debug("Fetching data from {}", url);
            Connection connection = client.json(url);
            PageResponse response = jsonMapper.readValue(connection.execute().body(), PageResponse.class);
            List<PikabuThread> pageThreads = pageParser.content(Jsoup.parse(response.getHtml()));
            if (pageThreads.size() != response.getThreadIds().size()) {
                LOGGER.warn("Parsed threads is not equal json thread count. Json = {}, parsed = {}",
                    response.getThreadIds().size(), pageThreads.size());
            }

            List<Long> threadIds = pageThreads.stream().map(PikabuThread::getId).collect(Collectors.toList());
            Set<PikabuThread> saved = new HashSet<>(threadIds.size());
            repository.findByIdIn(threadIds).stream().forEach(saved::add);
            pageThreads.stream().filter(t -> !saved.contains(t)).forEach(newThreads::add);

            Thread.sleep(requestIntervalMsec);
        }

        LOGGER.info("Saving {} threads", newThreads.size());
        for (PikabuThread thread : newThreads) {
            commentService.addComment(thread);
        }
        repository.save(newThreads);
    }
}
