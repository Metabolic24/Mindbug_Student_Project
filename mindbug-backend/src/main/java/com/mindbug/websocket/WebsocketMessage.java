package com.mindbug.websocket;

public class WebsocketMessage {
    // Message 
    private String messageID;
    private Object data;


    public WebsocketMessage(String messageID, Object data) {
        this.messageID = messageID;
        this.data = data;
    }


    public String getMessageID() {
        return messageID;
    }


    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public Object getData() {
        return data;
    }


    public void setData(Object data) {
        this.data = data;
    }

    

    
}
