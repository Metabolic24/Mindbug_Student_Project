package org.metacorp.mindbug.service.effect.impl;
import org.metacorp.mindbug.utils.AppUtils;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoAttackEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.game.CardService;

import java.util.ArrayList;
import java.util.List;

/**
 * Effect resolver for NoAttackEffect
 */
public class NoAttackEffectResolver extends EffectResolver<NoAttackEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public NoAttackEffectResolver(NoAttackEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.effectSource = card;

        CardKeyword keyword = effect.getKeyword();
        List<CardInstance> affectedCards  = new ArrayList<>();
        for (Player opponent: card.getOwner().getOpponent(game.getPlayers())) {
                affectedCards.addAll(opponent.getBoard());

        }
      
        if (keyword != null) {
            affectedCards = affectedCards.stream().filter(cardInstance -> cardInstance.hasKeyword(keyword)).toList();
        }

        if (effect.isLowest()) {
            affectedCards = CardService.getLowestCards(affectedCards);
        }

        resolve(game, affectedCards);
    }

    private void resolve(Game game, List<CardInstance> cards) {
        for (CardInstance card : cards) {
            card.setAbleToAttack(false);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
