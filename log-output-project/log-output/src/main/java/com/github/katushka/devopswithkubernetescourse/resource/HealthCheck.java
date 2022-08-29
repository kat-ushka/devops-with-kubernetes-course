package com.github.katushka.devopswithkubernetescourse.resource;

import com.github.katushka.devopswithkubernetescourse.service.PingPongService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/healthz")
public class HealthCheck {

    private final PingPongService pingPongService;

    public HealthCheck() {
        pingPongService = new PingPongService();
    }

    @GET
    public Response healthCheck() {
        if (pingPongService.isServiceAvailable()) {
            return Response.ok().entity("Pingpong service is available").build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Pingpong service is unavailable").build();
    }
}
