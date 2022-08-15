package com.github.katushka.devopswithkubernetescourse.pingpong.database;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class ConnectionFactory {

    private BasicDataSource dataSource;

    private final Logger logger = LogManager.getLogger(getClass());

    @PostConstruct
    public void init() {
        final String dbUrl = System.getenv("DB_URL");
        final String dbUser = System.getenv("DB_USER");
        final String dbUserPassword = getCleanedUserPassword();

        if (Strings.isNotBlank(dbUrl) && Strings.isNotBlank(dbUser) && Strings.isNotBlank(dbUserPassword)) {
            logger.atDebug().log("Environment vars:\n\tDB_URL is {},\n\tDB_USER is {},\n\tDB_USER_PASSWORD is {}",
                    dbUrl, dbUser, dbUserPassword);

            dataSource = new BasicDataSource();
            dataSource.setDriver(new Driver());
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbUserPassword);
            dataSource.setMinIdle(1);
            dataSource.setMaxIdle(10);
            dataSource.setMaxOpenPreparedStatements(10);
        } else {
            logger.atError().log("Environment vars statuses: DB_URL is {}, DB_USER is {}, DB_USER_PASSWORD is {}",
                    Strings.isBlank(dbUrl) ? "not set": "set",
                    Strings.isBlank(dbUser) ? "not set": "set",
                    Strings.isBlank(dbUserPassword) ? "not set": "set");
        }
    }

    /**
     * Kubernetes adds a `\n` character to the secret value,
     * so it needs to be cut off.
     * @return a password cleaned from the additional last `\n`
     */
    private static String getCleanedUserPassword() {
        final String pass = System.getenv("DB_USER_PASSWORD");
        if (pass.endsWith("\n")) {
            return pass.substring(0, pass.indexOf("\n"));
        }
        return pass;
    }

    public void connect(DatabaseAction action) throws SQLException {
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

    public interface DatabaseAction {
        void perform(Connection connection) throws SQLException;
    }
}
