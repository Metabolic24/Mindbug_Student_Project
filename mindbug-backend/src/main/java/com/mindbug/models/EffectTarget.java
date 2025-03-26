package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum EffectTarget {
    SELF("self"),
    OPPONENT("opponent"),
    ALL("all"),
    OPPONENT_CREATURE("opponent_creature"),
    OPPONENT_HAND("opponent_hand"),
    ALLIES("allies"),
    DISCARD("discard");

    private final String jsonValue;

    EffectTarget(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static EffectTarget fromJson(String value) {
        return Arrays.stream(values())
            .filter(e -> e.jsonValue.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid EffectTarget: " + value));
    }
}
