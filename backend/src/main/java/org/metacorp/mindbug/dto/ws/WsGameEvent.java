package org.metacorp.mindbug.dto.ws;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.dto.GameStateDTO;

/**
 * DTO for WebSocket game event
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class WsGameEvent {
    @NonNull
    private WsGameEventType type;
    @NonNull
    private GameStateDTO state;
}
