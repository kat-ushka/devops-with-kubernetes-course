package com.github.katushka.devopswithkubernetescourse;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationPath("/*")
public class LogOutputApplication extends Application {

    private final Logger logger;
    private final String randomString = UUID.randomUUID().toString();

    public LogOutputApplication() {
        logger = LogManager.getLogger(getClass());

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            logger.atDebug().log(randomString);
        }, 0, 5, TimeUnit.SECONDS);
    }
}
