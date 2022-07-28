package com.github.katushka.devopswithkubernetescourse.todoproject;

import com.github.katushka.devopswithkubernetescourse.todoproject.service.ServerPortService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ToDoProjectApplication {

    private static final Logger logger = LogManager.getLogger(ToDoProjectApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ToDoProjectApplication.class, args);
        ServerPortService portService = context.getBean(ServerPortService.class);
        logger.atDebug().log(new FormattedMessage("Server started in port %s", portService.getPort()));
    }

}
