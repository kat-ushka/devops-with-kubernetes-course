package com.github.katushka.devopswithkubernetescourse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Path("/")
public class GeneratedStringResource {

    private final String randomString = UUID.randomUUID().toString();
    private Instant lastTimestamp = Instant.now();

    public String getCode() {
        while (lastTimestamp.plus(5, ChronoUnit.SECONDS).isBefore(Instant.now())) {
            lastTimestamp = lastTimestamp.plus(5, ChronoUnit.SECONDS);
        }
        return lastTimestamp.toString() + " " + randomString;
    }

    @GET
    public Response getCodeResponse() {
        return Response.ok().entity(getCode()).build();
    }
}
