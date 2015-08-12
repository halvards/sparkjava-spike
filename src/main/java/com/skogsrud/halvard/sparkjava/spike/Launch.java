package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.before;
import static spark.SparkBase.port;

public class Launch {

    public static void main(String[] args) throws Exception {
        port(PortResolver.getPort(args));
        new Launch().run();
    }

    public void run() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        new HelloWorld().run();
        new Books(objectMapper).run();
        new Image().run();
        new Articles(objectMapper).run();

        disableCaching();
    }

    /**
     * Disables caching across HTTP 1.0 and 1.1 client and proxy caches
     */
    private void disableCaching() {
        before((request, response) -> {
            response.header("cache-control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            response.header("pragma", "no-cache"); // HTTP 1.0
            response.header("expires", "0"); // HTTP 1.0 proxies
        });
    }
}
