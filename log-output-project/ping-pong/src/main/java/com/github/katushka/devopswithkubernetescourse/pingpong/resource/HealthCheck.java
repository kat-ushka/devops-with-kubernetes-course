package com.github.katushka.devopswithkubernetescourse.pingpong.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class HealthCheck {

    @GET
    public Response healthCheck() {
        return Response.ok().entity("It works!").build();
    }

    @GET @Path("/pingpong")
    public Response healthCheckPingpong() {
        return Response.ok().entity("It also works!").build();
    }
}
