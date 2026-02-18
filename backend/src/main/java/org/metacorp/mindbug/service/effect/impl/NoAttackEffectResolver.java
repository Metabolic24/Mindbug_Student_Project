package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoAttackEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.utils.CardUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * Effect resolver for NoAttackEffect
 */
public class NoAttackEffectResolver extends EffectResolver<NoAttackEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public NoAttackEffectResolver(NoAttackEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        CardKeyword keyword = effect.getKeyword();
        Player opponent = effectSource.getOwner().getOpponent(game.getPlayers());
        List<CardInstance> affectedCards = opponent.getBoard();

        if (keyword != null) {
            affectedCards = affectedCards.stream().filter(cardInstance -> cardInstance.hasKeyword(keyword)).toList();
        }

        if (effect.isLowest()) {
            affectedCards = CardUtils.getLowestCards(affectedCards);
        }

        resolve(game, affectedCards);
    }

    private void resolve(Game game, List<CardInstance> cards) {
        Logger logger = game.getLogger();

        for (CardInstance card : cards) {
            card.setAbleToAttack(false);

            logger.debug("{} no more able to attack", card);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
