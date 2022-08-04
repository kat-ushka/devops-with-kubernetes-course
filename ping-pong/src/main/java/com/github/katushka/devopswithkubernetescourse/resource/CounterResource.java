package com.github.katushka.devopswithkubernetescourse.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Path("/")
public class CounterResource {

    private int counter = 0;

    private final String pingsFilepath;

    private final Logger logger = LogManager.getLogger(getClass());

    public CounterResource(String pingsFilepath) {
        this.pingsFilepath = pingsFilepath;
        writePingsToFile();
    }

    @GET
    public Response increaseAndReturn() {
        ++counter;
        writePingsToFile();
        return Response.ok().entity("pong " + counter).build();
    }

    private void writePingsToFile() {
        final File file = new File(pingsFilepath);
        try (FileWriter writer = new FileWriter(pingsFilepath, false)) {
            writer.write(String.valueOf(counter));
            logger.atDebug().log("Put new pings value in " + file.getAbsolutePath());
        } catch (IOException e) {
            logger.atError().log(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
