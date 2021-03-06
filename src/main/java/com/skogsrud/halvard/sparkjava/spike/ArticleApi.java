package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;

public class ArticleApi {
    private final ObjectMapper objectMapper;
    private final Map<String, Article> articles = new HashMap<>();
    private final Map<String, Map<String, byte[]>> images = new HashMap<>();

    public ArticleApi(ObjectMapper objectMapper) {
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

        get("/articles/:id/images/:filename", (request, response) -> {
            String id = request.params(":id");
            String filename = request.params(":filename");
            Map<String, byte[]> imageMap = images.get(id);
            if (imageMap == null) {
                response.status(404);
                return "Article with id [" + id + "] not found";
            }
            byte[] image = imageMap.get(filename);
            if (image == null) {
                response.status(404);
                return "Image with filename [" + filename + "] not found";
            }
            response.type("image/jpeg");
            try (ServletOutputStream out = response.raw().getOutputStream()) {
                IOUtils.write(image, out);
            }
            return response.raw();
        });

        get("/articles/:id/images", (request, response) -> {
            String id = request.params(":id");
            Map<String, byte[]> imageMap = images.get(id);
            if (imageMap == null) {
                response.status(404);
                return "Article with id [" + id + "] not found";
            }
            response.type("application/json");
            return imageMap.keySet();
        }, objectMapper::writeValueAsString);
    }

    private Article processArticleAndImage(spark.Request request, String id) throws IOException, ServletException {
        request.raw().setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
        Part articlePart = request.raw().getPart("article");
//        Article article = objectMapper.readValue(articlePart.getInputStream(), Article.class);
        Object articlePartJson = Configuration.defaultConfiguration().jsonProvider().parse(articlePart.getInputStream(), "UTF-8");
        String title = JsonPath.read(articlePartJson, "$.title");
        String body = JsonPath.read(articlePartJson, "$.body");
        Article article = new Article(title, body);
        articles.put(id, article);
        MultiPartInputStreamParser.MultiPart imagePart = (MultiPartInputStreamParser.MultiPart) request.raw().getPart("image");
        String filename = imagePart.getContentDispositionFilename();
        byte[] image = IOUtils.toByteArray(imagePart.getInputStream());
        HashMap<String, byte[]> imageMap = new HashMap<String, byte[]>() {{
            put(filename, image);
        }};
        images.put(id, imageMap);
        return article;
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) {
        port(PortResolver.getPort(args));
        new ArticleApi(new ObjectMapper()).run();
    }
}
