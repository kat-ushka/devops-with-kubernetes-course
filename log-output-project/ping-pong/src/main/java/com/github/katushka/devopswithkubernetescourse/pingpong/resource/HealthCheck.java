package com.github.katushka.devopswithkubernetescourse.pingpong.resource;

import com.github.katushka.devopswithkubernetescourse.pingpong.database.ConnectionFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/healthz")
public class HealthCheck {

    @Inject
    private ConnectionFactory factory;

    @GET
    public Response healthCheck() {
        if (factory.isConnectionAvailable()) {
            return Response.ok().entity("Database connection established").build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Cannot establish connection to the database").build();
    }
}
