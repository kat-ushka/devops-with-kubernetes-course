package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class CounterResource {

    private int counter = 0;

    @GET
    public Response increaseAndReturn() {
        return Response.ok().entity("pong " + ++counter).build();
    }
}
