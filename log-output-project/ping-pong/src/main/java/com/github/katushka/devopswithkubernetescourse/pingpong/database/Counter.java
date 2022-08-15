package com.github.katushka.devopswithkubernetescourse.pingpong.database;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@RequestScoped
public class Counter {

    @Inject
    private ConnectionFactory factory;

    private final String getCounterSQL = "SELECT id, counter FROM counter";
    private final String createCounterSQL = "INSERT INTO counter (counter) VALUES(?)";
    private final String updateCounterSQL = "UPDATE counter SET counter = ? WHERE id = ?";

    public int getIncrementedValue() throws SQLException {
        final AtomicInteger counterValue = new AtomicInteger();
        factory.connect(connection -> {
            counterValue.set(incrementCounter(connection));
        });
        return counterValue.get();
    }

    public int getValue() throws SQLException {
        final AtomicInteger counterValue = new AtomicInteger();
        factory.connect(connection -> {
            counterValue.set(getCounterValue(connection));
        });
        return counterValue.get();
    }

    private int getCounterValue(Connection connection) throws SQLException {
        ResultSet result = connection.createStatement().executeQuery(getCounterSQL);
        if (result.next()) {
            int counter = result.getInt("counter");
            if (result.next()) {
                throw new SQLException("There are more than 1 ping_counter objects in the database!");
            }
            return counter;
        } else {
            createCounter(connection);
        }
        return 0;
    }

    private void createCounter(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(createCounterSQL);
        statement.setInt(1, 0);
        statement.executeUpdate();
    }

    private int incrementCounter(Connection connection) throws SQLException {
        ResultSet result = connection.createStatement().executeQuery(getCounterSQL);
        if (result.next()) {
            int counter = result.getInt("counter") + 1;
            int id = result.getInt("id");
            PreparedStatement statement = connection.prepareStatement(updateCounterSQL);
            statement.setInt(1, counter);
            statement.setInt(2, id);
            statement.executeUpdate();
            if (result.next()) {
                throw new SQLException("There are more than 1 ping_counter objects in the database!");
            }
            return counter;
        } else {
            createCounter(connection);
            return 0;
        }
    }
}
