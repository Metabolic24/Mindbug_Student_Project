package org.metacorp.mindbug.model.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.model.ai.AiLevel;
import org.metacorp.mindbug.service.game.ai.AiResolver;
import org.metacorp.mindbug.service.game.ai.BasicAiResolver;
import org.metacorp.mindbug.service.game.ai.RandomAiResolver;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiPlayer extends Player {

    private AiResolver resolver;

    /**
     * Constructor
     */
    public AiPlayer(PlayerLightDTO playerDTO, AiLevel aiLevel) {
        super(playerDTO);
        resolver = switch (aiLevel) {
            case RANDOM ->  new RandomAiResolver();
            case BASIC ->   new BasicAiResolver();
        };
    }

    @Override
    public boolean isAI() {
        return true;
    }
}
