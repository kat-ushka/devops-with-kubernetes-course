package com.github.katushka.devopswithkubernetescourse;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationPath("/*")
public class PingPongApplication extends Application {

    public PingPongApplication() {
        Logger logger = LogManager.getLogger(getClass());
        logger.atDebug().log("Application version is 1.4");
    }
}
