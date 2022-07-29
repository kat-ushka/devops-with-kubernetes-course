package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

@Path("/")
public class GeneratedStringResource {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String randomString = UUID.randomUUID().toString();

    private final String filepath;

    public GeneratedStringResource() {
        filepath = System.getProperty("filepath", "/usr/src/app/files/timestamp");
    }

    public String getCode() {
        final File file = new File(filepath);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine() + " " + randomString;
            }
        } catch (IOException e) {
            logger.atError().log(e.getMessage(), e);
        }

        return "Failed to get timestamp from " + file.getAbsolutePath();
    }

    @GET
    public Response getCodeResponse() {
        return Response.ok().entity(getCode()).build();
    }
}
