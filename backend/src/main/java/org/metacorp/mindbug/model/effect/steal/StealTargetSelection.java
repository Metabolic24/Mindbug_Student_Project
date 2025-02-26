package org.metacorp.mindbug.model.effect.steal;

/** Enumeration that describes how steal targets are chosen */
public enum StealTargetSelection {
    SELF,       // Targets are chosen by the player that owns the card with the steal effect
    OPPONENT,   // Targets are chosen by the opponent of the player that owns the card with the steal effect
    RANDOM      // Targets are chosen randomly
}
