package org.metacorp.mindbug.card.effect.destroy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.choice.Choice;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;
import org.metacorp.mindbug.choice.SimultaneousChoice;
import org.metacorp.mindbug.player.Player;

import java.util.ArrayList;
import java.util.List;

/** Effect that may destroy one or more cards */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DestroyEffect extends AbstractEffect implements ResolvableEffect {
    public final static String TYPE = "DESTROY";

    private Integer value;          // The number of cards to destroy, -1 if all cards should be destroyed
    private boolean lessAllies;     // Effect is active if player has less allies than the opponent
    private boolean lowest;         // Destroy the card(s) with lowest power
    private boolean selfAllowed;    // Can this effect affect current player cards
    private Integer min;            // The minimum power of card(s) to be destroyed
    private Integer max;            // The maximum power of card(s) to be destroyed

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player currentPlayer = card.getOwner();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        if (lessAllies && !(currentPlayer.getBoard().size() < opponent.getBoard().size())) {
            return;
        }

        if (lowest) {
            List<CardInstance> lowestCards = selfAllowed ? Utils.getLowestCards(game.getPlayers()) :
                    opponent.getLowestCards();
            destroyCards(lowestCards, game, currentPlayer);
        } else {
            List<CardInstance> availableCards = new ArrayList<>();
            for (CardInstance currentCard : opponent.getBoard()) {
                if (min != null && currentCard.getPower() < min ||
                        max != null && currentCard.getPower() > max) {
                    continue;
                }

                availableCards.add(currentCard);
            }

            if (selfAllowed) {
                for (CardInstance currentCard : card.getOwner().getBoard()) {
                    if (min != null && currentCard.getPower() < min ||
                            max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (!availableCards.isEmpty()) {
                if (availableCards.size() <= value) {
                    destroyCards(availableCards, game, currentPlayer);
                } else {
                    game.setChoiceList(new ChoiceList(currentPlayer, value, Choice.getChoicesFromCards(availableCards, ChoiceLocation.BOARD), this, card));
                }
            }
        }
    }

    private void destroyCards(List<CardInstance> cards, Game game, Player currentPlayer) {
        List<CardInstance> cardsWithEffect = new ArrayList<>();
        for (CardInstance card : cards) {
            card.getOwner().getBoard().remove(card);
            card.getOwner().getDiscardPile().add(card);

            if (card.hasEffects(EffectTiming.DEFEATED)) {
                cardsWithEffect.add(card);
            }
        }

        if (!cardsWithEffect.isEmpty()) {
            if (cardsWithEffect.size() > 1) {
                SimultaneousChoice choice = new SimultaneousChoice(currentPlayer, EffectTiming.DEFEATED);
                choice.addAll(Choice.getChoicesFromCards(cardsWithEffect, ChoiceLocation.DISCARD));
                game.setChoice(choice);
            } else {
                game.addEffectsToQueue(cardsWithEffect.getFirst(), EffectTiming.DEFEATED);
            }
        }
    }

    @Override
    public void resolve(ChoiceList choices) {
        //TODO To be implemented
    }
}
