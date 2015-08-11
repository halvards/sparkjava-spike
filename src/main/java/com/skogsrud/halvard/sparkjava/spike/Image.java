package com.skogsrud.halvard.sparkjava.spike;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;

import static spark.Spark.get;
import static spark.SparkBase.port;

public class Image {
    public void run() throws Exception {
        get("/image", (request, response) -> {
//            String format = "jpg".equals(request.params(":format")) ? "jpeg" : request.params(":format");
            String format = "jpeg";

            response.status(200);
            response.type("image/" + format);

            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillOval(64, 64, 128, 128);
            graphics.dispose();
            try (ServletOutputStream out = response.raw().getOutputStream()) {
                ImageIO.write(image, format, out);
            }

            return response.raw();
        });
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) throws Exception {
        port(PortResolver.getPort(args));
        new Image().run();
    }
}
