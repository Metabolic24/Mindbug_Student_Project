package org.metacorp.mindbug.card.effect;

/** Enumeration for effect types */
public enum EffectType {
    DESTROY, DISABLE_TIMING, DISCARD, DRAW, GAIN, INFLICT, KEYWORD_UP, NO_ATTACK, NO_BLOCK, POWER_UP, REVIVE, STEAL;
    // Warning : Jackson uses constants for types that are located in each corresponding effect class
}
