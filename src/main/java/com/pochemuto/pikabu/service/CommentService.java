package com.pochemuto.pikabu.service;

import com.pochemuto.pikabu.dao.PikabuThread;
import com.pochemuto.pikabu.dao.PikabuThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private PikabuThreadRepository repository;

    private BlockingDeque<PikabuThread> queue = new LinkedBlockingDeque<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean shutdown = false;

    public void addComment(PikabuThread thread) {
        queue.offer(thread);
    }

    @PostConstruct
    private void init() {
        executorService.submit((Runnable) () -> {
            try {
                PikabuThread thread;
                while ((thread = queue.poll(5, TimeUnit.SECONDS)) != null && !shutdown) {
                    sendComment(thread);
                }
            } catch (InterruptedException e) {
                LOGGER.error("interrupted", e);
            }
            LOGGER.info("sending comment thread finished");
        });
    }

    private void sendComment(PikabuThread thread) {
        //repository.saveAndFlush(thread);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        executorService.shutdownNow();
        shutdown = true;
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        LOGGER.warn("Not commented: " + queue.stream().map(PikabuThread::toString).collect(Collectors.joining(", ")));
    }
}
