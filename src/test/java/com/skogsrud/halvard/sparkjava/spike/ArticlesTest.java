package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static spark.Spark.stop;
import static spark.SparkBase.awaitInitialization;

public class ArticlesTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static int port;

    @Test
    public void testMultipart() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost createArticleRequest = new HttpPost("http://localhost:" + port + "/articles");
            String articleAsString = objectMapper.writeValueAsString(new Article("article title", "article body"));
            String fileName = "image.jpg";
            byte[] imageBytes = IOUtils.toByteArray(getClass().getResourceAsStream("/" + fileName));
            HttpEntity requestEntity = MultipartEntityBuilder.create()
                    .addTextBody("article", articleAsString, ContentType.APPLICATION_JSON)
                    .addPart(FormBodyPartBuilder.create()
                            .setName("image")
                            .setBody(new ByteArrayBody(imageBytes, ContentType.create("image/jpeg"), fileName))
                            .setField("Content-Disposition", "form-data; name=\"image\"; filename=\"" + fileName + "\"; size=" + imageBytes.length)
                            .build())
                    .build();
            createArticleRequest.setEntity(requestEntity);
            try (CloseableHttpResponse createArticleResponse = httpClient.execute(createArticleRequest)) {
                assertThat(createArticleResponse.getStatusLine().getStatusCode(), equalTo(201));
                String location = createArticleResponse.getFirstHeader("location").getValue();
//                System.out.println("Retrieving article from: " + location);
                HttpGet getArticleRequest = new HttpGet(location);
                try (CloseableHttpResponse getArticleResponse = httpClient.execute(getArticleRequest)) {
                    assertThat(getArticleResponse.getStatusLine().getStatusCode(), equalTo(200));
                    assertThat(getArticleResponse.getFirstHeader("content-type").getValue(), equalTo("application/json"));
                }
                String imageUrl = "http://localhost:" + port + "/articles/images/" + fileName;
//                System.out.println("Retrieving image from:   " + imageUrl);
                HttpGet getImageRequest = new HttpGet(imageUrl);
                try (CloseableHttpResponse getImageResponse = httpClient.execute(getImageRequest)) {
                    assertThat(getImageResponse.getStatusLine().getStatusCode(), equalTo(200));
                    assertThat(getImageResponse.getFirstHeader("content-type").getValue(), equalTo("image/jpeg"));
                    assertThat(IOUtils.toByteArray(getImageResponse.getEntity().getContent()), equalTo(imageBytes));
                }
            }
        }
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        port = PortResolver.findOpenPort();
        Articles.main(new String[] { String.valueOf(port) });
        awaitInitialization();
    }

    @AfterClass
    public static void afterClass() {
        stop();
    }
}
