package com.mindbug.services.wsmessages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mindbug.websocket.WebsocketMessage;

import lombok.Getter;
import lombok.Setter;

@Scope("prototype")
@Service
@Getter @Setter
public class WSMessageManager {
    private String channel;

    @Autowired
    private SimpMessagingTemplate template;

    public void sendMessage(String message, Object data) {
        WebsocketMessage wsMessage = new WebsocketMessage(message, data);
        this.template.convertAndSend(channel, wsMessage);
    }

    public void sendMessage(WebsocketMessage wsMessage) {
        this.template.convertAndSend(channel, wsMessage);
    }
}
