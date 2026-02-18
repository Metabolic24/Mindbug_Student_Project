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
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.metacorp.mindbug.service.game.CardService.defeatCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for DestroyEffect
 */
public class DestroyEffectResolver extends EffectResolver<DestroyEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public DestroyEffectResolver(DestroyEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        Integer value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        boolean selfAllowed = effect.isSelfAllowed();
        boolean allies = effect.isAllies();

        Player sourceOwner = effectSource.getOwner();
        Player opponent = sourceOwner.getOpponent(game.getPlayers());

        if (effect.isLessAllies() && !(sourceOwner.getBoard().size() < opponent.getBoard().size())) {
            return;
        }

        if (effect.isItself()) {
            destroyCards(game, Collections.singletonList(effectSource));
        } else if (effect.isLowest()) {
            Set<Player> affectedPlayers = selfAllowed ? new HashSet<>(game.getPlayers()) : Collections.singleton(opponent);
            destroyCards(game, CardUtils.getLowestCards(affectedPlayers));
        } else {
            List<CardInstance> availableCards = new ArrayList<>();

            if (!allies) {
                for (CardInstance currentCard : opponent.getBoard()) {
                    if (min != null && currentCard.getPower() < min
                            || max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (allies || selfAllowed) {
                for (CardInstance currentCard : effectSource.getOwner().getBoard()) {
                    if (min != null && currentCard.getPower() < min
                            || max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (!availableCards.isEmpty()) {
                if (availableCards.size() <= value || value < 0) {
                    destroyCards(game, availableCards);
                } else {
                    game.setChoice(new TargetChoice(sourceOwner, effectSource, this, value, new HashSet<>(availableCards)));
                    game.getLogger().debug("Player {} must choose {} card(s) to destroy (available targets : {})", getLoggablePlayer(sourceOwner), value, getLoggableCards(availableCards));
                }
            }
        }
    }

    private void destroyCards(Game game, List<CardInstance> cards) {
        String loggableEffectSource = getLoggableCard(effectSource);
        for (CardInstance card : cards) {
            game.getLogger().debug("{} effect destroys {}", loggableEffectSource, getLoggableCard(card));
            defeatCard(card, game);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        destroyCards(game, chosenTargets);
    }
}
