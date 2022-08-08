package com.github.katushka.devopswithkubernetescourse.todoproject.beans;

import com.github.katushka.devopswithkubernetescourse.todoproject.services.ImageService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;

@Named
@RequestScoped
public class Image {

    @Inject
    private ImageService imageService;

    public String getLocation() throws IOException {
        File image = new File(imageService.getTodayImage());
        return "/images/" + image.getName();
    }
}
