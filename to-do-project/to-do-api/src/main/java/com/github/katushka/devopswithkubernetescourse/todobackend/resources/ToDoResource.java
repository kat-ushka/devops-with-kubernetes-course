package com.github.katushka.devopswithkubernetescourse.todobackend.resources;

import com.github.katushka.devopswithkubernetescourse.todobackend.database.ToDos;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/todos")
public class ToDoResource {

    private final Logger logger = LogManager.getLogger(getClass());

    @Inject
    private ToDos toDoList;

    @GET
    public Response getToDos() {
        try {
            return Response.ok().entity(toDoList.getToDos()).build();
        } catch (Exception e) {
            logger.atError().withThrowable(e).log("Failed to get ToDo list due to exception: {}", e.getMessage());
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createToDo(String toDoText) {
        try {
            toDoList.createToDo(toDoText);
            logger.atDebug().log("A new todo was created\n\t{}", toDoText);
            return Response.ok().build();
        } catch (Exception e) {
            logger.atError().withThrowable(e).log("Failed to create ToDo due to exception: {}", e.getMessage());
            return Response.serverError().entity(e).build();
        }
    }
}
