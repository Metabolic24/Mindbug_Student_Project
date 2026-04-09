package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.PlayerChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for DisableTimingEffect
 */
public class DiscardEffectResolver extends EffectResolver<DiscardEffect> implements ResolvableEffect<List<CardInstance>> {
   
   
    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public DiscardEffectResolver(DiscardEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
        
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        
        
        if (effect.isSelf()) {
         
            //Self discard
            choseCardTODiscard( game ,effectSource.getOwner());
            
        }
        else {
            if (effect.isEachEnemy()) {
                // Each enemy discard
                for (Player opponent : game.getOpponents()) {
                  
                    choseCardTODiscard(game,opponent);
                }
                
            }
            else {
                if (game.getOpponents().size() == 1) {
                    // If there is only one opponent, we directly make him discard without asking the player to choose
                  
                    choseCardTODiscard(game,game.getOpponents().getFirst());
                    
                }
                else {
                    //The player car target a player with no card in hand
                    game.setChoice(new PlayerChoice( effectSource.getOwner(), effectSource, this,game.getOpponents()));
                    game.getLogger().debug("Player {} must choose an oppponnent to target ",
                            getLoggablePlayer(effectSource.getOwner()));
                }
             
                
        
            }
            
        }

        
    }
    public void choseCardTODiscard(Game game, Player targetPlayer) {
        int value = effect.getValue();
        List<CardInstance> availableCards = effect.isDrawPile() ? targetPlayer.getDrawPile() : targetPlayer.getHand();

        if (availableCards.size() <= value || value == -1) {
            resolve(game, new ArrayList<>(availableCards));
        } else if (effect.isDrawPile()) {
            resolve(game, new ArrayList<>(availableCards.subList(0, value)));
        } else {
            game.setChoice(new TargetChoice(targetPlayer, effectSource, this, value, new HashSet<>(availableCards)));
            game.getLogger().debug("Player {} must choose {} card(s) to discard (available targets : {})",
                    getLoggablePlayer(targetPlayer), value, getLoggableCards(availableCards));
        }
    }

    
    public void resolve(Game game,  Player targetPlayer) {

    
        if (targetPlayer != null) {
            choseCardTODiscard(game,targetPlayer);
        }
    

       
    }
    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        String loggableEffectSource = getLoggableCard(effectSource);
        

        for (CardInstance card : chosenTargets) {
            Player cardOwner = card.getOwner();

            if (effect.isDrawPile()) {
                cardOwner.getDrawPile().remove(card);
            } else {
                cardOwner.getHand().remove(card);
            }

            cardOwner.getDiscardPile().add(card);

            game.getLogger().debug("{} discarded due to {} effect", getLoggableCard(card), loggableEffectSource);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, chosenTargets);
    }
    
}