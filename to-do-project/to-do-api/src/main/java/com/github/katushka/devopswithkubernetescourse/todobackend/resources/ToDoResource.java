package com.github.katushka.devopswithkubernetescourse.todobackend.resources;

import com.github.katushka.devopswithkubernetescourse.todoapi.beans.ToDo;
import com.github.katushka.devopswithkubernetescourse.todobackend.database.ToDos;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

@Path("/todos")
public class ToDoResource {

    private final Logger logger = LogManager.getLogger(getClass());

    @Inject
    private ToDos toDoList;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToDo> getToDos() {
        try {
            return toDoList.getToDos();
        } catch (SQLException e) {
            logger.atError().withThrowable(e).log("Failed to get ToDo list due to exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createToDo(String toDoText) {
        try {
            toDoList.createToDo(toDoText);
        } catch (SQLException e) {
            logger.atError().withThrowable(e).log("Failed to create ToDo due to exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
