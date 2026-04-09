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
import org.metacorp.mindbug.service.effect.ResolvableEffectWithTargetPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for DisableTimingEffect
 */
public class DiscardEffectResolver extends EffectResolver<DiscardEffect> implements ResolvableEffectWithTargetPlayer<List<CardInstance>> {

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
        Player sourceOwner = effectSource.getOwner();

        if (effect.isSelf()) {
            // Self discard
            chooseCardsToDiscard(game, sourceOwner);
        } else {
            List<Player> opponents = sourceOwner.getOpponents(game.getPlayers());
            if (opponents.size() == 1) {
                // If there is only one opponent, we directly make him discard without asking the player to choose
                chooseCardsToDiscard(game, opponents.getFirst());
            } else {
                //The player car target a player with no card in hand
                game.setChoice(new PlayerChoice(effectSource.getOwner(), effectSource, this, opponents));
                game.getLogger().debug("Player {} must choose an oppponnent to target ",
                        getLoggablePlayer(effectSource.getOwner()));
            }
        }
    }

    @Override
    public void selectPlayer(Game game, Player targetPlayer) {
        if (targetPlayer != null) {
            chooseCardsToDiscard(game, targetPlayer);
        }
    }

    private void chooseCardsToDiscard(Game game, Player targetPlayer) {
        int value = effect.getValue();
        List<CardInstance> availableCards = effect.isDrawPile() ? targetPlayer.getDrawPile() : targetPlayer.getHand();

        // Set effect value to the number of cards owned by the target player
        if (effect.isEachEnemy()) {
            value = targetPlayer.getBoard().size();
        }

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