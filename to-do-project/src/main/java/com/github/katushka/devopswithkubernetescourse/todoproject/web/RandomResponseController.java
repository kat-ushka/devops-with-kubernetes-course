package com.github.katushka.devopswithkubernetescourse.todoproject.web;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.UUID;

@Path("/")
@Singleton
public class RandomResponseController {

    private final String appString = UUID.randomUUID().toString();

    @GET
    public String getRandomResponse() {
        final String requestString = UUID.randomUUID().toString();
        return String.format("Application 1%s. Request 2%s.", appString, requestString);
    }
}
