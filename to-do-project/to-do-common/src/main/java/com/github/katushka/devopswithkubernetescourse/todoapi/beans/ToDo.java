package com.github.katushka.devopswithkubernetescourse.todoapi.beans;

import java.io.Serializable;

public class ToDo implements Serializable {

    private String id;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
