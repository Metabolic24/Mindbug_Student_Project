package org.metacorp.mindbug.service.effect.impl;
import org.metacorp.mindbug.utils.AppUtils;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.Collections;

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
        this.effectSource = effectSource;

        if (effect.isItself()) {
            giveCard(game, effectSource);
        }
    }

    private void giveCard(Game game, CardInstance cardToGive) {
      
        Player opponent = AppUtils.ChosenOpponent(game,cardToGive.getOwner());
        cardToGive.setOwner(opponent);
        game.getCurrentPlayer().getBoard().remove(cardToGive);
        opponent.getBoard().add(cardToGive);

        HistoryService.logEffect(game, effect.getType(), effectSource, Collections.singleton(cardToGive));
    }
}
