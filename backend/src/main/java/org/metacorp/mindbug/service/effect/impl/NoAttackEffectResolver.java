package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.NoAttackEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

/**
 * Effect resolver for NoAttackEffect
 */
public class NoAttackEffectResolver extends GenericEffectResolver<NoAttackEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public NoAttackEffectResolver(NoAttackEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card) {
        if (effect.isLowest()) {
            Player opponent = card.getOwner().getOpponent(game.getPlayers());
            for (CardInstance lowestCard : opponent.getLowestCards()) {
                lowestCard.setAbleToAttack(false);
            }
        }
    }
}
