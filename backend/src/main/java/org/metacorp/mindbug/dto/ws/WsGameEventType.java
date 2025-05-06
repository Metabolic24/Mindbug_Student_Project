package org.metacorp.mindbug.dto.ws;

/**
 * Enumeration for WebSocket event type
 */
public enum WsGameEventType {
    CARD_PICKED, CARD_PLAYED, ATTACK_DECLARED, CARD_DESTROYED, EFFECT_RESOLVED, CHOICE, NEW_TURN, LP_DOWN, FINISHED;
}
