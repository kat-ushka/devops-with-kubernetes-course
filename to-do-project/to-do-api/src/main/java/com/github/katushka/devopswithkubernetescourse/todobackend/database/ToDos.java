package com.github.katushka.devopswithkubernetescourse.todobackend.database;

import com.github.katushka.devopswithkubernetescourse.todoapi.beans.ToDo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ToDos {

    @Inject
    private ConnectionFactory factory;

    public List<ToDo> getToDos() throws SQLException {
        return factory.connect(connection -> {
            final String selectAll = "SELECT * FROM todos WHERE is_completed = FALSE";
            final List<ToDo> toDoList = new ArrayList<>();
            try (ResultSet resultSet = connection.createStatement().executeQuery(selectAll)) {
                while (resultSet.next()) {
                    ToDo toDo = new ToDo();
                    toDo.setId(resultSet.getInt("id"));
                    toDo.setText(resultSet.getString("text"));
                    toDoList.add(toDo);
                }
            }
            return toDoList;
        });
    }

    public void createToDo(String text) throws SQLException {
        factory.connect(connection -> {
            final String createSql = "INSERT INTO todos (text) VALUES (?)";

            try (PreparedStatement statement = connection.prepareStatement(createSql)) {
                statement.setString(1, text);
                statement.executeUpdate();
            }
        });
    }
}
