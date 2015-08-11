package com.skogsrud.halvard.sparkjava.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.SparkBase;

import static spark.Spark.before;
import static spark.SparkBase.port;

public class Launch {
    public static void main(String[] args) throws Exception {
        new Launch().run();
    }

    public void run() throws Exception {
        port(getPort());

        ObjectMapper objectMapper = new ObjectMapper();

        new HelloWorld().run();
        new Books(objectMapper).run();
        new Image().run();

        disableCaching();
    }

    /**
     * Use port from 'PORT' environment variable or 'port' system property, default is 4567.
     * Specify port 0 for a randomly assigned port.
     */
    private int getPort() {
        int port = SparkBase.SPARK_DEFAULT_PORT; // 4567
        if (System.getProperty("port") != null) {
            port = Integer.getInteger(System.getProperty("port"));
        }
        if (System.getenv("PORT") != null) {
            port = Integer.getInteger(System.getenv("PORT"));
        }
        return port;
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
