package com.github.katushka.devopswithkubernetescourse.todoapi.beans;

import java.io.Serializable;

public class ToDo implements Serializable {

    private Integer id;
    private String text;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
