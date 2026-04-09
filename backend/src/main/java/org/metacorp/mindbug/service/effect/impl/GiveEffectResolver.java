package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.PlayerChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.utils.AppUtils;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.effect.ResolvableEffectWithTargetPlayer;

import java.util.Collections;
import java.util.List;

import javax.smartcardio.Card;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for GiveEffect
 */
public class GiveEffectResolver extends EffectResolver<GiveEffect> implements ResolvableEffectWithTargetPlayer<List<CardInstance>> {


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
            giveCard(game, effectSource);
        }
    }

    private void giveCard(Game game, CardInstance cardToGive) {
       
          if (game.getOpponents().size() == 1) {             
                    
                    EnnemyGiveCard (game, game.getOpponents().getFirst());
                    
                }
                else {
                    //The player car target a player with no card in hand
                    game.setChoice(new PlayerChoice( effectSource.getOwner(), effectSource, this, game.getOpponents()));
                    game.getLogger().debug("Player {} must choose an oppponnent to target ",
                            getLoggablePlayer(effectSource.getOwner()));
                }          
    }

     private void EnnemyGiveCard (Game game, Player opponent) {
        effectSource.setOwner(opponent);
        game.getCurrentPlayer().getBoard().remove(effectSource);
        opponent.getBoard().add(effectSource);

        game.getLogger().debug("{} card has been given to {} due to its effect", getLoggableCard(effectSource), getLoggablePlayer(opponent));

        HistoryService.logEffect(game, effect.getType(), effectSource, Collections.singleton(effectSource));
    }
    @Override
    public void resolve(Game game,  Player targetPlayer) {    
       EnnemyGiveCard ( game, targetPlayer);      
    }
    @Override
    public void resolve(Game game,  List<CardInstance> chosenCards) {       
    }
}
