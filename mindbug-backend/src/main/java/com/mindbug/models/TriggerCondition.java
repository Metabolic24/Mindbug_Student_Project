package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum TriggerCondition {
    PLAY("Play"),
    ATTACK("Attack"),
    DEFEATED("Defeated"),
    PASSIVE("Passive");

    private final String jsonValue;

    TriggerCondition(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static TriggerCondition fromJson(String value) {
        return Arrays.stream(values())
            .filter(e -> e.jsonValue.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid TriggerCondition: " + value));
    }
}
