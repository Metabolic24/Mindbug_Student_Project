package org.metacorp.mindbug.dto.ws;

/**
 * Enumeration for WebSocket event type
 */
public enum WsGameEventType {
    STATE, CARD_PICKED, CARD_PLAYED, ATTACK_DECLARED, WAITING_ATTACK_RESOLUTION, CARD_DESTROYED, EFFECT_RESOLVED, CHOICE, NEW_TURN, LP_DOWN, FINISHED;
}
