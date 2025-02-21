package com.mindbug.services.wsmessages;

public enum GameWSMessage {
    NEW_GAME("newGame"),
    MATCH_FOUND("MATCH_FOUND");

    private final String label;

    GameWSMessage(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


}
