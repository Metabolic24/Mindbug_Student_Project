package com.mindbug.websocket;

public enum GameWSMessage {
    NEW_GAME("newGame"),
    MATCH_FOUND("MATCH_FOUND"),
    NEW_TURN("NEW_TURN"),
    ASK_BLOCK("ASK_BLOCK"),
    ATTACKED("ATTACKED"),
    CARD_DRAWED("CARD_DRAWED"),
    DIDNT_BLOCK("DIDNT_BLOCK"),
    BLOCKED("BLOCKED"),
    PLAYER_LIFE_UPDATED("PLAYER_LIFE_UPDATED"),
    CARD_DESTROYED("CARD_DESTROYED"),
    GAME_OVER("GAME_OVER");
    
    private final String label;

    GameWSMessage(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
