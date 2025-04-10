package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum OperationType {
    SET("set"),
    EQUALIZE("equalize");

    private final String jsonValue;

    OperationType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static OperationType fromJson(String value) {
        return Arrays.stream(values())
            .filter(e -> e.jsonValue.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid OperationType: " + value));
    }
}
