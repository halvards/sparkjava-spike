package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Article {
    private final String title;
    private final String body;
//    private final ZonedDateTime date;

    @JsonCreator
    public Article(@JsonProperty("title") String title, @JsonProperty("body") String body) {
        this.title = title;
        this.body = body;
//        this.date = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

//    public ZonedDateTime getDate() {
//        return date;
//    }
}
