package org.metacorp.mindbug.card.effect;

/** Enumeration for card effect timing */
public enum EffectTiming {
    PLAY,       // Triggers when card is played
    ATTACK,     // Triggers before the card attacks
    DEFEATED,   // Triggers after the card is defeated
    PASSIVE,    // Always considered as triggered when on board
    DISCARD,    // Always considered as triggered when in discard pile
    LIFE_LOST   // Triggers when a life point is lost
}
