package org.metacorp.mindbug.model.effect;

/**
 * Enumeration for effect types
 */
public enum EffectType {
    BOUNCE, DESTROY, DISABLE_TIMING, DISCARD, DRAW, GAIN, INFLICT, KEYWORD_UP, NO_ATTACK, NO_BLOCK, POWER_UP, PROTECTION, REVIVE, STEAL
    // Warning : Jackson uses constants for types that are located in each corresponding effect class
}
