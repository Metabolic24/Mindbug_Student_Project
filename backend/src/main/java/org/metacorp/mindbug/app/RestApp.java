package org.metacorp.mindbug.app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.websocket.WsGameEndpoint;
import org.metacorp.mindbug.websocket.WsJoinEndpoint;

import java.io.IOException;
import java.net.URI;

/**
 * Mindbug application that launches a REST and WS server and expects to receive REST requests to make the game progress.<br>
 * WebSockets are only used to notify client application(s) if any.
 */
public class RestApp {
    // Ports for REST and WS servers
    public static final Integer HTTP_PORT = 8080;

    /**
     * Creates the Grizzly HTTP server that exposes the JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    private static HttpServer createHttpServer() {
        // Create a resource config that scans for JAX-RS resources and providers in the package
        final ResourceConfig rc = new ResourceConfig().packages("org.metacorp.mindbug.controller", "org.metacorp.mindbug.service", "org.metacorp.mindbug.exception", "org.metacorp.mindbug.websocket");

        // Create a service locator so we can inject dependencies
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        ServiceLocatorUtilities.addClasses(locator, GameService.class);
        // Create and start a new instance of the Grizzly HTTP server
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:" + HTTP_PORT), rc, locator, false);

        registerWebSockets(server, locator);

        return server;
    }

    /**
     * Register web socket endpoints in the server
     *
     * @param server  the HTTP server
     * @param locator the HK2 service locator
     */
    private static void registerWebSockets(HttpServer server, ServiceLocator locator) {
        // Register WebSocket add-on in Grizzly listener
        server.getListener("grizzly").registerAddOn(new WebSocketAddOn());

        // Register each WS endpoint
        WebSocketEngine.getEngine().register("/ws", "/game/*", new WsGameEndpoint(locator.getService(GameService.class)));
        WebSocketEngine.getEngine().register("/ws", "/join", new WsJoinEndpoint(locator.getService(GameService.class)));
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = null;

        try {
            // Create an HTTP server that will expose both REST and WS APIs
            server = createHttpServer();
            server.start();

            System.out.printf("Jersey app started at http://localhost:%d/%s\n", HTTP_PORT, "game");
            System.out.printf("WebSocket app started at ws://localhost:%d/%s\n", HTTP_PORT, "ws");

            System.out.println("Hit enter to stop it...");
            System.in.read();
        } finally {
            if (server != null) {
                server.shutdownNow();
            }
        }
    }
}
