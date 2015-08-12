package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;

public class Articles {
    private final ObjectMapper objectMapper;
    private final Map<String, Article> articles = new HashMap<>();
    private final Map<String, byte[]> images = new HashMap<>();

    public Articles(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void run() {
        post("/articles", (request, response) -> {
            String id = UUID.randomUUID().toString();
            Article article = processArticleAndImage(request, id);
            response.status(201);
            response.type("application/json");
            response.header("location", request.url() + "/" + id);
            return new HashMap<String, Article>() {{
                put(id, article);
            }};
        }, objectMapper::writeValueAsString);

        put("/articles/:id", (request, response) -> {
            String id = request.params(":id");
            if (!articles.containsKey(id)) {
                response.status(404);
                return "Article with id [" + id + "] not found";

            }
            Article article = processArticleAndImage(request, id);
            response.status(200);
            response.type("application/json");
            return new HashMap<String, Article>() {{
                put(id, article);
            }};
        }, objectMapper::writeValueAsString);

        get("/articles/:id", (request, response) -> {
            String id = request.params(":id");
            Article article = articles.get(id);
            if (article == null) {
                response.status(404);
                return "Article with id [" + id + "] not found";
            }
            response.status(200);
            response.type("application/json");
            return article;
        }, objectMapper::writeValueAsString);

        get("/articles", (request, response) -> articles, objectMapper::writeValueAsString);

        delete("/articles/:id", (request, response) -> {
            String id = request.params(":id");
            Article article = articles.remove(id);
            if (article == null) {
                response.status(404);
                return "Article with id [" + id + "] not found";
            }
            return "Article with id [" + id + "] deleted";
        });
    }

    private Article processArticleAndImage(spark.Request request, String id) throws IOException, ServletException {
        request.raw().setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
        Part articlePart = request.raw().getPart("article");
        Article article = objectMapper.readValue(articlePart.getInputStream(), Article.class);
        articles.put(id, article);
        Part imagePart = request.raw().getPart("image");
        byte[] image = IOUtils.toByteArray(imagePart.getInputStream());
        images.put(imagePart.getHeader("filename"), image);
        return article;
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) {
        port(PortResolver.getPort(args));
        new Articles(new ObjectMapper()).run();
    }
}
