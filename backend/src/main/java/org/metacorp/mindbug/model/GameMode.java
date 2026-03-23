package org.metacorp.mindbug.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enumeration for game mode (1v1, 2v2 ...)
 */
public enum GameMode {
    DUEL("duel",2),
    TEAM("team",4);

    private final String key;

    @Getter
    private final int requiredPlayersCount;

    GameMode(String key, int requiredPlayer){
        this.key = key;
        this.requiredPlayersCount = requiredPlayer;
    }

    public static GameMode fromKey(String key) {
        return "team".equals(key) ? TEAM : DUEL;
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}