package com.github.katushka.devopswithkubernetescourse.todoproject.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RandomResponseController {

    private final String appString = UUID.randomUUID().toString();

    @GetMapping("/")
    public String getRandomResponse() {
        final String requestString = UUID.randomUUID().toString();
        return String.format("Application 1%s. Request 2%s.", appString, requestString);
    }
}
