package com.github.katushka.devopswithkubernetescourse.pingpong.resource;

import com.github.katushka.devopswithkubernetescourse.pingpong.database.Counter;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

@Path("/counter")
public class CounterResource {
    @Inject private Counter counter;
    private final Logger logger = LogManager.getLogger(getClass());

    @GET @Path("/increment")
    public Response getIncrementedValue() throws SQLException {
        logger.atDebug().log("pings increasing is requested!");
        return Response.ok().entity(counter.getIncrementedValue()).build();
    }

    @GET
    public int getCounter() throws SQLException {
        logger.atDebug().log("pings count is requested!");
        return counter.getValue();
    }
}
