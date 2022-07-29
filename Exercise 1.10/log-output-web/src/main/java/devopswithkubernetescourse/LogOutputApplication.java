package devopswithkubernetescourse;

import com.github.katushka.devopswithkubernetescourse.resource.GeneratedStringResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/*")
public class LogOutputApplication extends Application {

    private final GeneratedStringResource service;

    public LogOutputApplication() {
        service = new GeneratedStringResource();
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>(super.getSingletons());
        singletons.add(service);
        return singletons;
    }
}
