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
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.impl.steal.StealBooleanChoiceResolver;
import org.metacorp.mindbug.service.effect.impl.steal.TargetChoiceResolver;
import org.metacorp.mindbug.service.game.EffectQueueService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for StealEffect
 */
public class StealEffectResolver extends EffectResolver<StealEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public StealEffectResolver(StealEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        int value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        StealTargetSelection selection = effect.getSelection();
        StealSource source = effect.getSource();

        Player cardOwner = effectSource.getOwner();
        Player opponent = cardOwner.getOpponent(game.getPlayers());

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
                stealCards(new ArrayList<>(availableCards), game, cardOwner, effectSource);
            } else if (selection == StealTargetSelection.RANDOM) {
                List<CardInstance> stolenCards = new ArrayList<>();
                Random randomGenerator = new Random();
                for (int i = 1; i <= value; i++) {
                    int index = randomGenerator.nextInt(cardsCount - i);
                    stolenCards.add(availableCards.remove(index));

                }
                stealCards(stolenCards, game, cardOwner, effectSource);
            } else {
                Player playerToChoose = (selection == null || selection == StealTargetSelection.SELF) ? cardOwner : opponent;

                TargetChoice choice = new TargetChoice(playerToChoose, effectSource, new TargetChoiceResolver(effect, cardOwner, effectSource),
                        value, new HashSet<>(availableCards));
                choice.setOptional(effect.isOptional());
                game.setChoice(choice);
                game.getLogger().debug("Player {} must choose {} cards to steal (targets : {})", getLoggablePlayer(playerToChoose), value, getLoggableCards(availableCards));
            }
        }
    }

    protected void stealCards(List<CardInstance> stolenCards, Game game, Player newOwner, CardInstance sourceCard) {
        boolean mustPlay = effect.isMustPlay();
        boolean mayPlay = effect.isMayPlay();

        Logger logger = game.getLogger();
        String loggableEffectSource = getLoggableCard(effectSource);

        for (CardInstance stolenCard : stolenCards) {
            Player oldOwner = stolenCard.getOwner();
            switch (effect.getSource()) {
                case DISCARD, SELF_DISCARD -> oldOwner.getDiscardPile().remove(stolenCard);
                case HAND -> oldOwner.getHand().remove(stolenCard);
                case BOARD -> oldOwner.getBoard().remove(stolenCard);
                default -> {
                } // Nothing to do
            }

            stolenCard.setOwner(newOwner);

            if (mustPlay) {
                newOwner.getBoard().add(stolenCard);
                if (effect.getSource() != StealSource.BOARD) {
                    // Add PLAY effects (if any) if player is allowed to trigger them
                    EffectQueueService.addBoardEffectsToQueue(stolenCard, EffectTiming.PLAY, game.getEffectQueue());
                }

                logger.debug("{} stolen and played by {} due to {} effect", getLoggableCard(stolenCard), getLoggablePlayer(newOwner), loggableEffectSource);
            } else if (mayPlay) {
                game.setChoice(new BooleanChoice(newOwner, sourceCard, new StealBooleanChoiceResolver(stolenCard, effectSource), stolenCard));
                logger.debug("{} must decide if stolen card {} will be added to hand or board", getLoggablePlayer(newOwner), getLoggableCard(stolenCard));
            } else {
                newOwner.getHand().add(stolenCard);
                logger.debug("{} stolen and drawn by {} due to {} effect", getLoggableCard(stolenCard), getLoggablePlayer(newOwner), loggableEffectSource);
            }
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, stolenCards);
    }
}
