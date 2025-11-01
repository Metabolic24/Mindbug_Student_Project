package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.EffectQueueService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that provides methods about cards during a game
 */
public class CardService {

    /**
     * Method triggered when a card is defeated
     *
     * @param card        the defeated card
     * @param effectQueue the effect queue
     */
    public static void defeatCard(CardInstance card, EffectQueue effectQueue) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            card.getOwner().addCardToDiscardPile(card);
            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, effectQueue);
        }
    }

    /**
     * Get the cards with the lowest power on boards
     *
     * @param players the players concerned by the request
     * @return the list containing the lowest cards of any player
     */
    public static List<CardInstance> getLowestCards(List<Player> players) {
        List<CardInstance> lowestCards = new ArrayList<>();
        int lowestPower = 10;

        for (Player player : players) {
            List<CardInstance> currentCards = player.getLowestCards(lowestPower);

            if (!currentCards.isEmpty()) {
                int currentPower = currentCards.getFirst().getPower();

                if (currentPower < lowestPower) {
                    lowestPower = currentPower;
                    lowestCards = new ArrayList<>(currentCards);
                } else if (currentPower == lowestPower) {
                    lowestCards.addAll(currentCards);
                }
            }
        }

        return lowestCards;
    }

    /**
     * Get all the passive effects from the cards list
     *
     * @param cards     the cards to analyze
     * @param inDiscard true if we look for EffectTiming.DISCARD, false if we look for EffectTiming.PASSIVE
     * @return the passive effects list
     */
    public static List<EffectsToApply> getPassiveEffects(List<CardInstance> cards, boolean inDiscard) {
        EffectTiming timing = inDiscard ? EffectTiming.DISCARD : EffectTiming.PASSIVE;

        List<EffectsToApply> passiveEffects = new ArrayList<>();
        cards.forEach(card -> passiveEffects.addAll(
                card.getEffects(timing).stream()
                        .map(cardEffect -> new EffectsToApply(Collections.singletonList(cardEffect), card, timing))
                        .toList())
        );

        return passiveEffects;
    }
}
