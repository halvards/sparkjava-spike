package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;

/**
 * Adapted from https://github.com/perwendel/spark
 */
public class Books {
    private static Map<String, Book> books = new HashMap<>();
    private final ObjectMapper objectMapper;

    public Books(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void run() throws Exception {
        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent as JSON, e.g., { "author": "Foo", "title": "Bar" }
        post("/books", "application/json", (request, response) -> {
            Book book = objectMapper.readValue(request.body(), Book.class);
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
        // author and title are sent as query parameters e.g. /books/<id>?author=Foo&title=Bar
        put("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = new Book(request.queryParams("author"), request.queryParams("title"));
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

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) throws Exception {
        new Books(new ObjectMapper()).run();
    }
}
