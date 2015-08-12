package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.port;
import static spark.Spark.post;

public class Articles {
    private final ObjectMapper objectMapper;
    private final Map<String, Article> articles = new HashMap<>();
    private final Map<String, byte[]> images = new HashMap<>();

    public Articles(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void run() {
        post("/articles", (request, response) -> {
            request.raw().setAttribute("org.eclipse.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
            Part article = request.raw().getPart("article");
            Part image = request.raw().getPart("image");
            for (String headerName : image.getHeaderNames()) {
                System.out.println(headerName + ": " + image.getHeader(headerName));
            }
            String id = UUID.randomUUID().toString();
            String text = IOUtils.toString(article.getInputStream());
            return text;
        });
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) {
        port(PortResolver.getPort(args));
        new Articles(new ObjectMapper()).run();
    }
}
