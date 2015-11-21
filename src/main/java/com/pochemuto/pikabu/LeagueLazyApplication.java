package com.pochemuto.pikabu;

import com.pochemuto.pikabu.service.NewThreadChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class LeagueLazyApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeagueLazyApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LeagueLazyApplication.class, args);
    }

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ApplicationContext context;

    @Value("${pikabu.check.intervalSec}")
    private int checkIntervalSec;

    @Override
    public void run(String... strings) throws Exception {
        LOGGER.info("Scheduled new thread checks with {} sec interval", checkIntervalSec);
        Runnable runnable = () -> {
            LOGGER.info("Checking new threads");
            NewThreadChecker threadChecker = context.getBean(NewThreadChecker.class);
            try {
                threadChecker.check();
            } catch (Exception ex) {
                LOGGER.error("Error when checking new threads", ex);
            }
        };
        executorService.scheduleWithFixedDelay(runnable, 0, checkIntervalSec, TimeUnit.SECONDS);
    }
}
