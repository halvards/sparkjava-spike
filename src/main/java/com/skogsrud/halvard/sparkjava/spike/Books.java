package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;

public class Books {
    private static Map<String, Book> books = new HashMap<>();
    private final ObjectMapper objectMapper;

    public Books(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void run() throws Exception {
        // Creates a new book resource, will return the id of the created resource.
        // author and title should be sent either:
        // 1. as JSON, e.g., { "author": "Foo", "title": "Bar" } ; or
        // 2. as form-encoded values in the request body, e.g., author=Foo&title=Bar
        post("/books", (request, response) -> {
            Book book = readBookFromRequestBody(request);
            String id = UUID.randomUUID().toString();
            books.put(id, book);
            response.status(201);
            return id;
        });

        // Gets the book resource for the provided id
        get("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.get(id);
            if (book == null) {
                response.status(404);
                return "Book with id [" + id + "] not found";
            }
            response.type("application/json");
            return book;
        }, objectMapper::writeValueAsString);

        // Updates the book resource for the provided id with new information
        // author and title should be sent as per the create 'post' route
        put("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = readBookFromRequestBody(request);
            if (books.get(id) != null) {
                books.put(id, book);
                return book;
            } else {
                response.status(404);
                return "Book with id [" + id + "] not found";
            }
        });

        // Deletes the book resource for the provided id
        delete("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.remove(id);
            if (book == null) {
                response.status(404);
                return "Book with id [" + id + "] not found";
            }
            return "Book with id [" + id + "] deleted";
        });

        // Gets all books
        get("/books", (request, response) -> {
            response.type("application/json");
            return books;
        }, objectMapper::writeValueAsString);
    }

    private Book readBookFromRequestBody(Request request) throws IOException {
        Book book = null;
        switch (request.contentType()) {
            case "application/json":
                book = objectMapper.readValue(request.body(), Book.class);
                break;
            case "application/x-www-form-urlencoded":
                book = new Book(request.queryParams("author"), request.queryParams("title"));
                break;
            default:
                halt(400, "Unsupported request content-type [" + request.contentType() + "]");
        }
        return book;
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) throws Exception {
        new Books(new ObjectMapper()).run();
    }
}
