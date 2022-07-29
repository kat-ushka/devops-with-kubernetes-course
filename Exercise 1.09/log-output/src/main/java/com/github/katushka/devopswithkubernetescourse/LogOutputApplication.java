package com.github.katushka.devopswithkubernetescourse;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationPath("/*")
public class LogOutputApplication extends Application {

    private final Logger logger;
    private final GeneratedStringResource service;

    public LogOutputApplication() {
        logger = LogManager.getLogger(getClass());
        service = new GeneratedStringResource();

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            final String currentCode = service.getCode();
            logger.atDebug().log(currentCode);
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>(super.getSingletons());
        singletons.add(service);
        return singletons;
    }
}
