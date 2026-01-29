package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.EffectResolver;

/**
 * Effect resolver for GiveEffect
 */
public class GiveEffectResolver extends EffectResolver<GiveEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public GiveEffectResolver(GiveEffect effect) {
        super(effect);
    }


    @Override
    public void apply(Game game, CardInstance effectSource, EffectTiming timing) {
        if (effect.isItself()) {
            giveCard(game, effectSource);
        }
    }

    private void giveCard(Game game, CardInstance cardToGive) {
        Player opponent = cardToGive.getOwner().getOpponent(game.getPlayers()).get(0);

        cardToGive.setOwner(opponent);
        game.getCurrentPlayer().getBoard().remove(cardToGive);
        opponent.getBoard().add(cardToGive);
    }
}
