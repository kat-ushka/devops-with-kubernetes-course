package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@ManagedBean
@SessionScoped
public class ToDoCatalog implements Serializable {

    private List<ToDo> toDoList;

    @PostConstruct
    public void init() {
        toDoList = new ArrayList<>();
        toDoList.add(new ToDo("To finish the dwk course"));
        toDoList.add(new ToDo("To do something else"));
    }

    public List<ToDo> getToDoList() {
        return Collections.unmodifiableList(toDoList);
    }
}
