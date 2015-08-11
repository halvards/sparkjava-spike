package com.skogsrud.halvard.sparkjava.spike;

import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class HelloWorld {
    public void run() throws Exception {
        get("/hello", (request, response) -> "Hello World");

        get("/hello7", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "Hello World without lambda";
            }
        });
    }

    /**
     * Use this main() method to run a server with only the routes defined in this class
     */
    public static void main(String[] args) throws Exception {
        port(PortResolver.getPort(args));
        new HelloWorld().run();
    }
}
