package com.skogsrud.halvard.sparkjava.spike;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;

import static spark.Spark.*;

public class Image {
    public static void main(String[] args) {
        get("/image", (request, response) -> {
            response.status(200);
            response.type("image/jpeg");

            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillOval(64, 64, 128, 128);
            try (ServletOutputStream out = response.raw().getOutputStream()) {
                ImageIO.write(image, "JPEG", out);
            }
            return response.raw();
        });

         before((request, response) -> {
            response.header("cache-control", "no-cache, no-store, must-revalidate");
        });
    }
}
