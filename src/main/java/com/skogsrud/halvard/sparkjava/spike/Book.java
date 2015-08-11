package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {
    private final String author;
    private final String title;

    @JsonCreator
    public Book(@JsonProperty("author") String author, @JsonProperty("title") String title) {
        this.author = author;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}
