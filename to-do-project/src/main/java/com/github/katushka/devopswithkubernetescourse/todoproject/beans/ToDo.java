package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ManagedBean
@SessionScoped
public class ToDo implements Serializable {

    private String text;

    public ToDo() {
    }

    public ToDo(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void save() {

    }
}
