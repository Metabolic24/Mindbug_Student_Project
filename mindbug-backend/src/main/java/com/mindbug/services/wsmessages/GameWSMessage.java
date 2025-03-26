package com.mindbug.services.wsmessages;

public enum GameWSMessage {
    NEW_GAME("newGame"),
    MATCH_FOUND("MATCH_FOUND"),
    NEW_TURN("NEW_TURN"),
    ASK_BLOCK("ASK_BLOCK"),
    ATTACKED("ATTACKED"),
    DIDNT_BLOCK("DIDNT_BLOCK"),
    BLOCKED("BLOCKED"),
    PLAYER_LIFE_UPDATED("PLAYER_LIFE_UPDATED"),
    CARD_DESTROYED("CARD_DESTROYED");
    




    private final String label;

    GameWSMessage(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
