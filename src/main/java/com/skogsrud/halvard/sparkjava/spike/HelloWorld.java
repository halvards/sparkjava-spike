package com.skogsrud.halvard.sparkjava.spike;

import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (request, response) -> "Hello World");
    }
}
