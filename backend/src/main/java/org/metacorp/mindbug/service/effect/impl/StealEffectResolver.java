package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.StealEffect;
import org.metacorp.mindbug.model.effect.steal.StealSource;
import org.metacorp.mindbug.model.effect.steal.StealTargetSelection;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.EffectQueueService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.impl.steal.StealBooleanChoiceResolver;
import org.metacorp.mindbug.service.effect.impl.steal.TargetChoiceResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Effect resolver for StealEffect
 */
public class StealEffectResolver extends EffectResolver<StealEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public StealEffectResolver(StealEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        int value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        StealTargetSelection selection = effect.getSelection();
        StealSource source = effect.getSource();

        Player cardOwner = card.getOwner();
        Player opponent = cardOwner.getOpponent(game.getPlayers()).get(0);

        List<CardInstance> availableCards = switch (source) {
            case DISCARD -> opponent.getDiscardPile();
            case HAND -> opponent.getHand();
            case SELF_DISCARD -> cardOwner.getDiscardPile();
            case BOARD -> opponent.getBoard();
        };

        if (min != null) {
            availableCards = availableCards.stream().filter(currentCard -> currentCard.getPower() >= min).collect(Collectors.toList());
        }

        if (max != null) {
            availableCards = availableCards.stream().filter(currentCard -> currentCard.getPower() <= max).collect(Collectors.toList());
        }

        int cardsCount = availableCards.size();
        if (cardsCount > 0) {
            if (!effect.isOptional() && (cardsCount <= value || value < 0)) {
                stealCards(new ArrayList<>(availableCards), game, cardOwner, card);
            } else if (selection == StealTargetSelection.RANDOM) {
                List<CardInstance> stolenCards = new ArrayList<>();
                Random randomGenerator = new Random();
                for (int i = 1; i <= value; i++) {
                    int index = randomGenerator.nextInt(cardsCount - i);
                    stolenCards.add(availableCards.remove(index));

                }
                stealCards(stolenCards, game, cardOwner, card);
            } else {
                Player playerToChoose = (selection == null || selection == StealTargetSelection.SELF) ? cardOwner : opponent;

                TargetChoice choice = new TargetChoice(playerToChoose, card, new TargetChoiceResolver(effect, cardOwner, card), value, new HashSet<>(availableCards));
                choice.setOptional(effect.isOptional());
                game.setChoice(choice);
            }
        }
    }

    protected void stealCards(List<CardInstance> stolenCards, Game game, Player newOwner, CardInstance sourceCard) {
        boolean mustPlay = effect.isMustPlay();
        boolean mayPlay = effect.isMayPlay();

        for (CardInstance stolenCard : stolenCards) {
            Player oldOwner = stolenCard.getOwner();
            switch (effect.getSource()) {
                case DISCARD, SELF_DISCARD -> oldOwner.getDiscardPile().remove(stolenCard);
                case HAND -> oldOwner.getHand().remove(stolenCard);
                case BOARD -> oldOwner.getBoard().remove(stolenCard);
            }

            stolenCard.setOwner(newOwner);

            if (mustPlay) {
                newOwner.getBoard().add(stolenCard);
                if (effect.getSource() != StealSource.BOARD) {
                    // Add PLAY effects (if any) if player is allowed to trigger them
                    EffectQueueService.addBoardEffectsToQueue(stolenCard, EffectTiming.PLAY, game.getEffectQueue());
                }
            } else if (mayPlay) {
                game.setChoice(new BooleanChoice(newOwner, sourceCard, new StealBooleanChoiceResolver(stolenCard), stolenCard));
            } else {
                newOwner.getHand().add(stolenCard);
            }
        }
    }
}
