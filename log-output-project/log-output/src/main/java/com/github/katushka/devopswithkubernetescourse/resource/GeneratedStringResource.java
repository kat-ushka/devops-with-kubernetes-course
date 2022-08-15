package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
@Path("/")
public class GeneratedStringResource {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String randomString = UUID.randomUUID().toString();

    private final String timestampFilepath;

    private final String pingPongUrl;

    public GeneratedStringResource() {
        timestampFilepath = Optional.ofNullable(System.getenv("TIME_STAMP_FILEPATH")).orElse("/usr/src/app/files/timestamp");
        logger.atDebug().log("timestampFilepath = {}", timestampFilepath);
        pingPongUrl = Optional.ofNullable(System.getenv("PINGS_URL")).orElse("http://localhost:8080/pingpong/api/counter");
        logger.atDebug().log("pingPongUrl = {}", pingPongUrl);
    }

    public String getCode() {
        return MessageFormat.format(
                "{0}\n{1}\t{2}\nPing / Pongs: {3}",
                Optional.ofNullable(System.getenv("MESSAGE")).orElse("No message"),
                readFile(timestampFilepath), randomString, getPingPongsCount());
    }

    private int getPingPongsCount() {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        logger.atDebug().log("Requesting ping-pongs at {}", pingPongUrl);

        WebTarget target = client.target(pingPongUrl);
        try (Response response = target.request().get()) {
            String value = response.readEntity(String.class);
            return Integer.parseInt(value);
        }
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
