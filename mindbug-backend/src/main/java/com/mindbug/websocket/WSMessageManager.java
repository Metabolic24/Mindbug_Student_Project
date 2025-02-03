package com.mindbug.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WSMessageManager {
    private String channel;
    private SimpMessagingTemplate template;

    public WSMessageManager(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void sendMessage(WebsocketMessage wsMessage) {
        this.template.convertAndSend(channel, wsMessage);
    }

    

    
}
