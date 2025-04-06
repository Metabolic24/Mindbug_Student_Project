package org.metacorp.mindbug.model;

import lombok.Getter;

/** Enumeration for Card Set names */
@Getter
public enum CardSetName {
    FIRST_CONTACT("first_contact");

    private final String key;

    CardSetName(String key) {
        this.key = key;
    }
}
