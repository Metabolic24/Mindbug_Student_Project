package org.metacorp.mindbug.service.effect.impl.steal;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.service.EffectQueueService;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

public class StealBooleanChoiceResolver implements ResolvableEffect<Boolean> {

    private final CardInstance card;

    /**
     * Constructor
     *
     * @param card the stolen card
     */
    public StealBooleanChoiceResolver(CardInstance card) {
        this.card = card;
    }

    @Override
    public void resolve(Game game, Boolean choiceData) {
        if (choiceData != null && choiceData) {
            card.getOwner().getBoard().add(card);
            // Add PLAY effects (if any) if player is allowed to trigger them
            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());
        } else {
            card.getOwner().getHand().add(card);
        }
    }
}
