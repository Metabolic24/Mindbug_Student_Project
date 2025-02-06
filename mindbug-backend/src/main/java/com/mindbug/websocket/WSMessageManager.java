package com.mindbug.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Scope("prototype")
@Service
public class WSMessageManager {
    private String channel;

    @Autowired
    private SimpMessagingTemplate template;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void sendMessage(String message, Object data) {
        WebsocketMessage wsMessage = new WebsocketMessage(message, data);
        this.template.convertAndSend(channel, wsMessage);
    }
}
