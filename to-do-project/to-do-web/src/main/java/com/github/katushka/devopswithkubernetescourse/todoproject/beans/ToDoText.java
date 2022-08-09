package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import com.github.katushka.devopswithkubernetescourse.todoproject.services.ToDoService;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ManagedBean
@SessionScoped
public class ToDoText implements Serializable {

    @Inject
    private ToDoService toDoService;

    private String text;

    public ToDoText() {
    }

    public ToDoText(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void save() {
        toDoService.createToDo(getText());
    }
}
