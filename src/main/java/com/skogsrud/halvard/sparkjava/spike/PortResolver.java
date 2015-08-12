package com.skogsrud.halvard.sparkjava.spike;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.SparkBase;

import java.io.IOException;
import java.net.ServerSocket;

public class PortResolver {
    private static final Logger LOG = LoggerFactory.getLogger(PortResolver.class);

    /**
     * Use port from 'PORT' environment variable, or 'port' system property, or first command line option.
     * Default is 4567.
     * Specify port 0 for a randomly assigned port.
     */
    public static int getPort(String[] args) {
        int port = SparkBase.SPARK_DEFAULT_PORT; // 4567
        try {
            if (System.getenv("PORT") != null) {
                port = Integer.parseInt(System.getenv("PORT"));
            } else if (System.getProperty("port") != null) {
                port = Integer.parseInt(System.getProperty("port"));
            } else if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Using default port " + port, e);
        }
        return port;
    }

    public static int findOpenPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
