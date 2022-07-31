package com.github.katushka.devopswithkubernetescourse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeStampGenerator {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String filepath;

    public TimeStampGenerator() {
        filepath = Optional.ofNullable(System.getenv("TIME_STAMP_FILEPATH")).orElse("/usr/src/app/files/timestamp");
    }

    private void startGenerating() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            final File file = new File(filepath);
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(Instant.now().toString());
                logger.atDebug().log("Put new timestamp in " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.atError().log(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        TimeStampGenerator instance = new TimeStampGenerator();
        instance.startGenerating();
    }
}
