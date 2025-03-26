package com.mindbug.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebsocketMessage {
    private String messageID;
    private Object data;

    public WebsocketMessage(String messageID) {
        this.messageID = messageID;
    }
}
