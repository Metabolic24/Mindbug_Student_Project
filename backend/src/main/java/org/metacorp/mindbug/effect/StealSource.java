package org.metacorp.mindbug.effect;

import org.metacorp.mindbug.choice.ChoiceLocation;

public enum StealSource {
    HAND, DISCARD, SELF_DISCARD, BOARD;

    public ChoiceLocation toChoiceLocation() {
        ChoiceLocation result = null;
        switch (this) {
            case HAND -> {
                result = ChoiceLocation.HAND;
            }
            case DISCARD, SELF_DISCARD -> {
                result = ChoiceLocation.DISCARD;
            }
            case BOARD -> {
                result = ChoiceLocation.BOARD;
            }
        }
        return result;
    }
}
