package org.metacorp.mindbug.model.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Describes an AI player action
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AiPlayerTurnAction {
    @NonNull
    private TurnActionType type;
    @NonNull
    private CardInstance target;
}
