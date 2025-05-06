package org.metacorp.mindbug.dto.ws;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * DTO for WebSocket game event, dedicated to a specific player
 */
@Getter
@Setter
public class WsPlayerGameEvent {
    @NonNull
    private WsGameEventType type;
    @NonNull
    private WsPlayerGameState state;

    public WsPlayerGameEvent(@NonNull final WsGameEventType type) {
        this.type = type;
    }
}
