package com.github.katushka.devopswithkubernetescourse.todoproject.servlets;

import com.github.katushka.devopswithkubernetescourse.todoproject.services.ImageService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

    @Inject
    private ImageService imageService;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String todayImagePath = imageService.getTodayImage();
        final File imageFile = new File(todayImagePath);
        response.setHeader("Content-Type", getServletContext().getMimeType(todayImagePath));
        response.setHeader("Content-Length", String.valueOf(imageFile.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + todayImagePath + "\"");
        Files.copy(imageFile.toPath(), response.getOutputStream());
    }
}