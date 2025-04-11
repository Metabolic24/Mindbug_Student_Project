package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum EffectAction {
    BLOCK("Block"),
    PLAY_EFFECT("Play_Effect");

    private final String jsonValue;

    EffectAction(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static EffectAction fromJson(String value) {
        return Arrays.stream(values())
            .filter(e -> e.jsonValue.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid EffectAction: " + value));
    }
}
