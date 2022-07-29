package com.github.katushka.devopswithkubernetescourse.todoproject;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@ApplicationPath("/*")
public class ToDoProjectApplication extends Application {

    public ToDoProjectApplication() {
        Logger logger = LogManager.getLogger(ToDoProjectApplication.class);
        final Optional<String> port = Optional.ofNullable(System.getenv("CATALINA_HTTP_PORT"));
        logger.atDebug().log("Server started in port " + port.orElse("8080"));
    }
}
