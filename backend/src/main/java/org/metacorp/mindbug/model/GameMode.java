package org.metacorp.mindbug.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GameMode {
    DUEL("duel",2),
    TEAM("team",4);

    private final String key;
    private final int requiredPlayer;

    GameMode(String key, int requiredPlayer){
        this.key = key;
        this.requiredPlayer = requiredPlayer;
    }

    public static GameMode fromKey(String key) {
        return switch (key) {
            case "duel" -> DUEL;
            case "team" -> TEAM;
            default -> null;
        };
    }

    public int getRequiredPlayer(){
        return requiredPlayer;
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}
