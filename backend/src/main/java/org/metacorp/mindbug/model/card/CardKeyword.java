package org.metacorp.mindbug.model.card;

/**
 * Enumeration for card keywords
 */
public enum CardKeyword {
    FRENZY,     // Card may attack twice
    HUNTER,     // Card may choose its target when attacking
    POISONOUS,  // Card defeats in any case the other creature during battle
    SNEAKY,     // Card can only be blocked by SNEAKY creatures
    TOUGH,      // Card survives the first time it would be defeated
    REVERSED    // Card gains fight against higher power but loses against lower one
}
