package org.metacorp.mindbug.service;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class GameWebSocketClient implements Closeable {
    private Session userSession;

    public GameWebSocketClient(String endpoint) throws URISyntaxException, DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        userSession = container.connectToServer(this, new URI(endpoint));
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     */
    @OnClose
    public void onClose() {
        this.userSession = null;
    }

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    @Override
    public void close() throws IOException {
        if (this.userSession != null) {
            this.userSession.close();
        }
    }
}
