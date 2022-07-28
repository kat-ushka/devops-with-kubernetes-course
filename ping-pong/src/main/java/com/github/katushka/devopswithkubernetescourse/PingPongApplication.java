package com.github.katushka.devopswithkubernetescourse;

import com.github.katushka.devopswithkubernetescourse.resource.CounterResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/*")
public class PingPongApplication extends Application {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>(super.getSingletons());
        singletons.add(new CounterResource());
        return singletons;
    }
}
