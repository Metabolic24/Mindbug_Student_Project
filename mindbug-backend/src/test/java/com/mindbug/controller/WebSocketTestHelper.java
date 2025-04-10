package com.mindbug.controller;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.client.Transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WebSocketTestHelper {
    private final String serverUrl;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final BlockingQueue<JsonNode> messages;
    

    public WebSocketTestHelper(int port) {
        this.serverUrl = "ws://localhost:" + port + "/mindbug-ws";
        this.messages = new LinkedBlockingQueue<>();

        // Configuration du client STOMP
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        List<Transport> transports = List.of(new WebSocketTransport(webSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void connect() throws Exception {
        stompSession = stompClient
                .connectAsync(serverUrl, new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        System.out.println("Connected to WebSocket!");
                    }
                    
                    @Override
                    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        System.out.println("Error: " + exception.getMessage());
                    }
                })
                .get(5, TimeUnit.SECONDS);
    }

    public void subscribe(String topic) {
        System.out.println("Subscribing to: " + topic);
        stompSession.subscribe(topic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return JsonNode.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                messages.offer((JsonNode) payload);
            }
        });
    }

    public void sendMessage(String destination, Object payload) {
        stompSession.send(destination, payload);
    }

    public JsonNode getNextMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return messages.poll(timeout, unit);
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        messages.clear();
    }

    public <T> T convertMessage(JsonNode message, Class<T> targetClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.treeToValue(message, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert message", e);
        }
    }
    public <T> T convertMessage(String message, Class<T> targetClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert message", e);
        }
    }
}