package com.github.katushka.devopswithkubernetescourse.todobackend.database;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

@ApplicationScoped
public class ConnectionFactory {

    private BasicDataSource dataSource;

    private final Logger logger = LogManager.getLogger(getClass());

    @PostConstruct
    public void init() {
        final String dbHost = getCleanedVariable("DB_HOST");
        final String dbPort = getCleanedVariable("DB_PORT");
        final String dbName = getCleanedVariable("DB_NAME");
        final String dbUser = getCleanedVariable("DB_USER");
        final String dbUserPassword = getCleanedVariable("DB_USER_PASSWORD");

        final String dbUrl = MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}", dbHost, dbPort, dbName);

        if (noneIsBlank(dbHost, dbPort, dbName, dbUser, dbUserPassword)) {
            logger.atDebug().log("""
                    Environment vars:
                        DB_HOST is {},
                        DB_PORT is {},
                        DB_NAME is {},
                        DB_USER is {}.
                    URL is {}""",
                    dbHost, dbPort, dbName, dbUser, dbUrl);
            dataSource = new BasicDataSource();
            dataSource.setDriver(new Driver());
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbUserPassword);
            dataSource.setMinIdle(1);
            dataSource.setMaxIdle(10);
            dataSource.setMaxOpenPreparedStatements(10);
        } else {
            logger.atError().log("""
                            Environment vars statuses:
                                DB_HOST is {},
                                DB_PORT is {},
                                DB_NAME is {},
                                DB_USER is {},
                                DB_USER_PASSWORD is {}""",
                    Strings.isBlank(dbHost) ? "not set": "set",
                    Strings.isBlank(dbPort) ? "not set": "set",
                    Strings.isBlank(dbName) ? "not set": "set",
                    Strings.isBlank(dbUser) ? "not set": "set",
                    Strings.isBlank(dbUserPassword) ? "not set": "set");
        }
    }

    /**
     * Kubernetes adds a `\n` character to the secret value,
     * so it needs to be cut off.
     * @return a password cleaned from the additional last `\n`
     */
    private static String getCleanedVariable(String envName) {
        final String pass = System.getenv(envName);
        if (pass.endsWith("\n")) {
            return pass.substring(0, pass.indexOf("\n"));
        }
        return pass;
    }

    private boolean noneIsBlank(String ... values) {
        for (String value: values) {
            if (Strings.isBlank(value)) {
                return false;
            }
        }
        return true;
    }

    public <T> T connect(DatabaseAction<T> action) throws SQLException {
        checkDataSource();

        try (Connection connection = dataSource.getConnection()) {
            return action.perform(connection);
        }
    }

    public void connect(VoidDatabaseAction action) throws SQLException {
        checkDataSource();

        try (Connection connection = dataSource.getConnection()) {
            action.perform(connection);
        }
    }

    private void checkDataSource() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("dataSource is not initialized!");
        }
    }

    public interface DatabaseAction<T> {
        T perform(Connection connection) throws SQLException;
    }

    public interface VoidDatabaseAction {
        void perform(Connection connection) throws SQLException;
    }
}
