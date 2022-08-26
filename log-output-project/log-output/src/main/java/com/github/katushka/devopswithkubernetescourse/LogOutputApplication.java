package com.github.katushka.devopswithkubernetescourse;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
public class LogOutputApplication extends Application {

    public LogOutputApplication() {
    }
}
