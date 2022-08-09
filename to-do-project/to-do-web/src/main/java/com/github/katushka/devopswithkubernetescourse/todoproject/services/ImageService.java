package com.github.katushka.devopswithkubernetescourse.todoproject.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@Named
public class ImageService {

    private final Logger logger = LogManager.getLogger(getClass());
    private final String filePath;

    public ImageService() {
        filePath = Optional.ofNullable(System.getenv("UPLOAD_LOCATION"))
                .orElse("/usr/src/app/files/to-do-today.jpg");
        logger.atDebug().log("Image will be stored at {}", filePath);
    }

    public String getTodayImage() throws IOException {
        updateImageIfRequired();
        return filePath;
    }

    private void updateImageIfRequired() throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            if (!Objects.isNull(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
        }

        if (Files.size(path) == 0
                || Files.getLastModifiedTime(path).toInstant().isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS))) {
            // 200 is a size of square image
            try(InputStream in = new URL("https://picsum.photos/400").openStream()){
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                logger.atDebug().log("Image is updated");
            }
        }
    }

    public String getWebLocation() throws IOException {
        File image = new File(getTodayImage());
        return "/images/" + image.getName();
    }
}
