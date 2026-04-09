package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.PlayerChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import java.util.Collections;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for GiveEffect
 */
public class GiveEffectResolver extends EffectResolver<GiveEffect> implements ResolvableEffect<Player> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public GiveEffectResolver(GiveEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        if (effect.isItself()) {
            if (game.getOpponents().size() == 1) {
                giveCard(game, game.getOpponents().getFirst());
            } else {
                //The player car target a player with no card in hand
                game.setChoice(new PlayerChoice(effectSource.getOwner(), effectSource, this, game.getOpponents()));
                game.getLogger().debug("Player {} must choose an oppponnent to target ",
                        getLoggablePlayer(effectSource.getOwner()));
            }
        }
    }

    private void giveCard(Game game, Player targetPlayer) {
        effectSource.setOwner(targetPlayer);
        game.getCurrentPlayer().getBoard().remove(effectSource);
        targetPlayer.getBoard().add(effectSource);

        game.getLogger().debug("{} card has been given to {} due to its effect", getLoggableCard(effectSource), getLoggablePlayer(targetPlayer));

        HistoryService.logEffect(game, effect.getType(), effectSource, Collections.singleton(effectSource));
    }

    @Override
    public void resolve(Game game, Player targetPlayer) {
        giveCard(game, targetPlayer);
    }
}
