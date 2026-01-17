package org.metacorp.mindbug.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.websocket.GameWebSocket;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiPlayer extends Player {

    private GameWebSocket gameWebSocket;

    /**
     * Constructor
     */
    public AiPlayer() {
        super(PlayerService.createPlayer("Michel"));
    }

    @Override
    public boolean isAI() {
        return true;
    }
}
