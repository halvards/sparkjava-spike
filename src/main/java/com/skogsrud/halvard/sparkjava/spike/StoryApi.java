package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

public class StoryApi {
    private final ObjectMapper objectMapper;
    private final Map<String, Story> stories = new HashMap<>();

    public StoryApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void run() throws Exception {
        post("/stories", (request, response) -> {
            Story story = createDocument(request.body());
            response.status(201);
            response.type("application/json");
            response.header("location", request.url() + "/" + story.getId().toString());
            HashMap<String, Story> storyWithData = new HashMap<>();
            storyWithData.put("data", story);
            return storyWithData;
        }, objectMapper::writeValueAsString);

        get("/stories/:id", (request, response) -> {
            String id = request.params("id");
            Story story = stories.get(id);
            if (story == null) {
                response.status(404);
                return "Story with id [" + id + "] not found";
            }
            response.type("application/json");
            return story;
        }, objectMapper::writeValueAsString);
    }

    private Story createDocument(String requestBody) throws Exception {
        Story story = new Story();
        story.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        story.setModifiedAt(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        story.setId(UUID.randomUUID().toString());
        story.setType("articles");
        Map<String, Object> requestBodyAsMap = objectMapper.readValue(requestBody, new TypeReference<LinkedHashMap<String, Object>>() { });
        story.setDocument(requestBodyAsMap);
        story.setSponsored(false);
        story.setShareUrl("http://shareurl.com");
        story.setRevision(UUID.randomUUID().toString());
        story.getLinks().setSelf("http://self.com");
        story.getLinks().setChannel("http://channel.com");
        story.getLinks().setSections(Arrays.asList("http://section1.com", "http://section2.com"));
        return story;
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) throws Exception {
        port(PortResolver.getPort(args));
        new StoryApi(new ObjectMapper()).run();
    }
}
