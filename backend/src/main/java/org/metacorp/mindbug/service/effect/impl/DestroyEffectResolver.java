
package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.CardService;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.metacorp.mindbug.service.game.CardService.defeatCard;

/**
 * Effect resolver for DestroyEffect
 */
public class DestroyEffectResolver extends EffectResolver<DestroyEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public DestroyEffectResolver(DestroyEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        Integer value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        boolean selfAllowed = effect.isSelfAllowed();
        boolean allies_touched = effect.isAllies();

        Player currentPlayer = card.getOwner();
       
        List<CardInstance> listopponentCard= new ArrayList<>();
        for (Player opponents: currentPlayer.getOpponent(game.getPlayers())) {
            listopponentCard.addAll(opponents.getBoard());
        }

        if (effect.isLessAllies() && !(currentPlayer.getBoard().size() <  listopponentCard.size())) {
            return;
        }

        if (effect.isItself()) {
            destroyCards(game, Collections.singletonList(card));
        } else if (effect.isLowest()) {

                Set<Player> affectedPlayers = new HashSet<>();

                // Tous les ennemis
                affectedPlayers.addAll(currentPlayer.getOpponent(game.getPlayers()));

                // Optionnellement soi-même
                if (selfAllowed) {
                    affectedPlayers.add(currentPlayer);
                    Player my_allie = currentPlayer.getAllie(game.getPlayers()) ;
                    if (my_allie!=null) {
                        affectedPlayers.add(my_allie);
                    }
                    
                }

                destroyCards(game, CardService.getLowestCards(affectedPlayers));
            } else {
            List<CardInstance> availableCards = new ArrayList<>();

            if (!allies_touched) {
                for (CardInstance currentCard :  listopponentCard) {
                    if (min != null && currentCard.getPower() < min ||
                            max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (allies_touched|| selfAllowed) {
                for (CardInstance currentCard : card.getOwner().getBoard()) {
                    if (min != null && currentCard.getPower() < min
                            || max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
                //if selfAllowed, target my allies
                Player my_allie = currentPlayer.getAllie(game.getPlayers()) ;
                    if (my_allie!=null) {
                        for (CardInstance currentCard : my_allie.getBoard()) {
                            if (min != null && currentCard.getPower() < min ||
                                    max != null && currentCard.getPower() > max) {
                                continue;
                            }

                            availableCards.add(currentCard);
                        }
                    }
                
            }

            if (!availableCards.isEmpty()) {
                if (availableCards.size() <= value || value < 0) {
                    destroyCards(game, availableCards);
                } else {
                    game.setChoice(new TargetChoice(currentPlayer, card, this, value, new HashSet<>(availableCards)));
                }
            }
        }
    }

    private void destroyCards(Game game, List<CardInstance> cards) {
        for (CardInstance card : cards) {
            defeatCard(card, game);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }

 
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        destroyCards(game, chosenTargets);
    }
}