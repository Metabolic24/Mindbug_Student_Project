package org.metacorp.mindbug.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.service.PlayerService;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiPlayer extends Player {

    /**
     * Constructor
     */
    public AiPlayer(PlayerLightDTO playerDTO) {
        super(playerDTO);
    }

    @Override
    public boolean isAI() {
        return true;
    }
}
