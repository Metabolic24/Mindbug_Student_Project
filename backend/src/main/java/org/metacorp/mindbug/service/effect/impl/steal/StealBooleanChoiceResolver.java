package org.metacorp.mindbug.service.effect.impl.steal;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.EffectQueueService;
import org.slf4j.Logger;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

public class StealBooleanChoiceResolver implements ResolvableEffect<Boolean> {

    private final CardInstance card;
    private final CardInstance effectSource;

    /**
     * Constructor
     *
     * @param card the stolen card
     */
    public StealBooleanChoiceResolver(CardInstance card, CardInstance effectSource) {
        this.card = card;
        this.effectSource = effectSource;
    }

    @Override
    public void resolve(Game game, Boolean choiceData) {
        Logger logger = game.getLogger();
        String loggableEffectSource = getLoggableCard(effectSource);

        if (choiceData != null && choiceData) {
            card.getOwner().getBoard().add(card);
            // Add PLAY effects (if any) if player is allowed to trigger them
            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());
            logger.debug("{} stolen and played by {} due to {} effect", getLoggableCard(card), getLoggablePlayer(card.getOwner()), loggableEffectSource);
        } else {
            card.getOwner().getHand().add(card);
            logger.debug("{} stolen and drawn by {} due to {} effect", getLoggableCard(card), getLoggablePlayer(card.getOwner()), loggableEffectSource);
        }
    }
}
