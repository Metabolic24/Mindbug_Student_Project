package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Service that provides methods about cards during a game
 */
public class CardService {

    /**
     * Method triggered when a card is defeated
     *
     * @param card the defeated card
     * @param game the game state
     */
    public static void defeatCard(CardInstance card, Game game) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            Player cardOwner = card.getOwner();
            if (card.getCard().isEvolution()) {
                cardOwner.getBoard().remove(card);
                cardOwner.getDiscardPile().add(card.getInitialCard());

                // Add the card back to the evolution cards list, as it may come back later in the game
                card.setOwner(null);
                game.getEvolutionCards().add(card);
            } else {
                cardOwner.addCardToDiscardPile(card);
            }

            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        }
    }

    public static List<CardInstance> getLowestCards(List<CardInstance> cards) {
        return getLowestCards(cards, Integer.MAX_VALUE);
    }

    public static List<CardInstance> getLowestCards(List<CardInstance> cards, int lowestPower) {
        List<CardInstance> lowestCards = new ArrayList<>();

        for (CardInstance card : cards) {
            if (card.getPower() < lowestPower) {
                lowestPower = card.getPower();
                lowestCards.clear();
                lowestCards.add(card);
            } else if (card.getPower() == lowestPower) {
                lowestCards.add(card);
            }
        }

        return lowestCards;
    }

    /**
     * Get the cards with the lowest power on boards
     *
     * @param players the players concerned by the request
     * @return the list containing the lowest cards of any player
     */
    public static List<CardInstance> getLowestCards(Set<Player> players) {
        List<CardInstance> lowestCards = new ArrayList<>();
        int lowestPower = Integer.MAX_VALUE;

        for (Player player : players) {
            List<CardInstance> currentCards = getLowestCards(player.getBoard(), lowestPower);

            if (!currentCards.isEmpty()) {
                int currentPower = currentCards.getFirst().getPower();

                if (currentPower < lowestPower) {
                    lowestPower = currentPower;
                    lowestCards = new ArrayList<>(currentCards);
                } else {
                    // currentPower is equal to lowestPower, so we add all retrieved cards to the list
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
