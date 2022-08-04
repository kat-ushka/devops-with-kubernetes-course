package com.github.katushka.devopswithkubernetescourse;

import com.github.katushka.devopswithkubernetescourse.resource.CounterResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ApplicationPath("/*")
public class PingPongApplication extends Application {

    private final CounterResource resource;

    public PingPongApplication() {
        final String pingsFilepath = Optional.ofNullable(System.getenv("PINGS_FILEPATH"))
                .orElse("/usr/src/app/files/pings");
        resource = new CounterResource(pingsFilepath);
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>(super.getSingletons());
        singletons.add(resource);
        return Collections.unmodifiableSet(singletons);
    }
}
