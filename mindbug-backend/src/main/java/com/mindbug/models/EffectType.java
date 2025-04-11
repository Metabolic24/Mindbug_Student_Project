package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EffectType {
    LIFE_POINTS("Life Points"),
    PREVENT_ACTION("Prevent Action"),
    CONTROL_CREATURE("Control Creature"),
    DESTROY_CREATURE("Destroy Creature"),
    DRAW_CARDS("Draw Cards"),
    POWER_MODIFICATION("Power Modification"),
    KEYWORD_GAIN("Keyword Gain"),
    STEAL_CARDS("Steal Cards"),
    DISCARD("Discard"),
    PLAY_FROM_DISCARD("Play From Discard");

    private final String jsonValue;

    EffectType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static EffectType fromJson(String value) {
        return Arrays.stream(values())
            .filter(effectType -> effectType.jsonValue.equalsIgnoreCase(value.replace("_", " ")))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid EffectType: " + value));
 
        }
}
