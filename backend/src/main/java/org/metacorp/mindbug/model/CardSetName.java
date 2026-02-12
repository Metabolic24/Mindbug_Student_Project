package org.metacorp.mindbug.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration for Card Set names
 */
public enum CardSetName {
    FIRST_CONTACT("first_contact"),
    BEYOND_EVOLUTION("beyond_evolution");;

    private final String key;

    CardSetName(String key) {
        this.key = key;
    }

    public static CardSetName fromKey(String key) {
        return switch (key) {
            case "first_contact" -> FIRST_CONTACT;
            case "beyond_evolution" -> BEYOND_EVOLUTION;
            default -> null;
        };
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}
