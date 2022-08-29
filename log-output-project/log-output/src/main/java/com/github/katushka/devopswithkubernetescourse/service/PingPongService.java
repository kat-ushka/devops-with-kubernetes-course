package com.github.katushka.devopswithkubernetescourse.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLEngineResult;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PingPongService {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String pingPongUrl;

    public PingPongService() {
        pingPongUrl = Optional.ofNullable(System.getenv("PINGS_URL"))
                .orElse("http://localhost:8080/pingpong/api/counter");
    }

    private Client getClient() {
        return ClientBuilder.newBuilder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public boolean isServiceAvailable() {
        Client client = getClient();
        try {
            WebTarget target = client.target(pingPongUrl);
            try (Response response = target.request().get()) {
                return response.getStatus() == Response.Status.OK.getStatusCode();
            }
        } catch (Exception ex) {
            logger.atError().withThrowable(ex)
                    .log("Failed to request ping-pongs at {} due to error {}", pingPongUrl, ex.getMessage());
            return false;
        } finally {
            client.close();
        }
    }

    public int getPingPongsCount() {
        Client client = getClient();

        logger.atDebug().log("Requesting ping-pongs at {}", pingPongUrl);

        WebTarget target = client.target(pingPongUrl);
        try (Response response = target.request().get()) {
            String value = response.readEntity(String.class);
            logger.atDebug().log("Ping-pongs are {}", value);
            return Integer.parseInt(value);
        }
    }
}
