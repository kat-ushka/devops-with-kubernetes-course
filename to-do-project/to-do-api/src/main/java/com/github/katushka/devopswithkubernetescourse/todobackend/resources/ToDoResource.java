package com.github.katushka.devopswithkubernetescourse.todobackend.resources;

import com.github.katushka.devopswithkubernetescourse.todoapi.beans.ToDo;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Singleton
@Path("/todos")
public class ToDoResource {

    private final Logger logger = LogManager.getLogger(getClass());
    private final List<ToDo> toDoList = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToDo> getToDos() {
        return Collections.unmodifiableList(toDoList);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String createToDo(String toDoText) {
        ToDo toDo = new ToDo();
        toDo.setId(UUID.randomUUID().toString());
        toDo.setText(toDoText);
        toDoList.add(toDo);

        logger.atDebug().log("{} todos in the list.", toDoList.size());

        return toDo.getId();
    }
}
