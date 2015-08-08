package com.skogsrud.halvard.sparkjava.spike;

import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (request, response) -> "Hello World");

        get("/hello7", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "Hello World without lambda";
            }
        });
    }
}
