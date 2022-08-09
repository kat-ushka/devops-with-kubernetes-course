package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
@Path("/")
public class CounterResource {

    private int counter = 0;

    private final Logger logger = LogManager.getLogger(getClass());

    @GET
    public Response increaseAndReturn() {
        logger.atDebug().log("pings increasing is requested!");
        ++counter;
        return Response.ok().entity("pong " + counter).build();
    }
    @GET @Path("/counter")
    public int getCounter() {
        logger.atDebug().log("pings count is requested!");
        return counter;
    }
}
