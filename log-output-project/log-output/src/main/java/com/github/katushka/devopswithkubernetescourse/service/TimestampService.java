package com.github.katushka.devopswithkubernetescourse.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class TimestampService {

    private final Logger logger = LogManager.getLogger(getClass());
    private final String timestampFilepath;

    public TimestampService() {
        timestampFilepath = Optional.ofNullable(System.getenv("TIME_STAMP_FILEPATH"))
                .orElse("/usr/src/app/files/timestamp");
        logger.atDebug().log("timestampFilepath = {}", timestampFilepath);
    }

    public String getTimestamp() {
        final File file = new File(timestampFilepath);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            }
        } catch (IOException e) {
            logger.atError().log(e.getMessage(), e);
            throw new RuntimeException("Failed to read "
                    + file.getAbsolutePath() + ". Exception is: " + e.getMessage(), e);
        }
        return "empty";
    }
}
