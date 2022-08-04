package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@Path("/")
@Singleton
public class GeneratedStringResource {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String randomString = UUID.randomUUID().toString();

    private final String timestampFilepath;
    private final String pingsFilepath;

    public GeneratedStringResource() {
        timestampFilepath = Optional.ofNullable(System.getenv("TIME_STAMP_FILEPATH")).orElse("/usr/src/app/files/timestamp");
        pingsFilepath = Optional.ofNullable(System.getenv("PINGS_FILEPATH")).orElse("/usr/src/app/files/pings");
    }

    public String getCode() {
        String response = readFile(timestampFilepath) + "      " + randomString;
        response += "\n";
        response += "Ping / Pongs: " + readFile(pingsFilepath);
        return response;
    }

    private String readFile(String filePath) {
        final File file = new File(filePath);
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

    @GET
    public Response getCodeResponse() {
        return Response.ok().entity(getCode()).build();
    }
}
