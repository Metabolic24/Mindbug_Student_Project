package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.BounceEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

public class BounceEffectResolver extends EffectResolver<BounceEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public BounceEffectResolver(BounceEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        int value = effect.getValue();
        Player cardOwner = effectSource.getOwner();
        Set<CardInstance> opponentCards = new HashSet<>(effectSource.getOwner().getOpponent(game.getPlayers()).getBoard());

        if (!opponentCards.isEmpty()) {
            if (opponentCards.size() <= value || value < 0) {
                bounceCards(game, opponentCards);
            } else {
                game.setChoice(new TargetChoice(cardOwner, effectSource, this, value, opponentCards));
                game.getLogger().debug("Player {} must choose {} card(s) to bounce (available targets : {})", getLoggablePlayer(cardOwner), value, getLoggableCards(opponentCards));
            }
        }
    }

    private void bounceCards(Game game, Set<CardInstance> cards) {
        Logger logger = game.getLogger();
        String loggableEffectSource = getLoggableCard(effectSource);

        for (CardInstance card : cards) {
            Player cardOwner = card.getOwner();
            cardOwner.getBoard().remove(card);
            cardOwner.getHand().add(card);

            logger.debug("Card {} bounced from the board to {} hand due to {} effect", getLoggableCard(card), getLoggablePlayer(cardOwner), loggableEffectSource);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        bounceCards(game, new HashSet<>(chosenTargets));
    }
}
