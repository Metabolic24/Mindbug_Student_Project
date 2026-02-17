package org.metacorp.mindbug.model.ai;

import lombok.Data;
import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Describes an AI player action
 */
@Data
public class AiPlayerTurnAction {
    private TurnActionType type;
    private CardInstance target;
}
