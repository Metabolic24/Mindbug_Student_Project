package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;
import org.metacorp.mindbug.model.modifier.PowerModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Effect resolver for PowerUpEffect
 */
public class PowerUpEffectResolver extends EffectResolver<PowerUpEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public PowerUpEffectResolver(PowerUpEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        int value = effect.getValue();
        boolean alone = effect.isAlone();
        boolean self = effect.isSelf();
        boolean selfTurn = effect.isSelfTurn();
        boolean allies = effect.isAllies();
        boolean forEachAlly = effect.isForEachAlly();
        boolean noMindbug = effect.isNoMindbug();
        Integer lifePoints = effect.getLifePoints();
        Integer enemiesCount = effect.getEnemiesCount();
        Integer alliesCount = effect.getAlliesCount();

        Player currentPlayer = effectSource.getOwner();
        Player opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
        int powerToAdd = value;

        if ((lifePoints != null && currentPlayer.getTeam().getLifePoints() > lifePoints)
                || (enemiesCount != null && opponentPlayer.getBoard().size() < enemiesCount)
                || (alliesCount != null && currentPlayer.getBoard().size() != alliesCount)
                || (alone && currentPlayer.getBoard().size() != 1)
                || (noMindbug && currentPlayer.getMindBugs() != 0)
                || (selfTurn && !currentPlayer.equals(game.getCurrentPlayer()))) {
            return;
        }

        if (forEachAlly) {
            int boardSize = currentPlayer.getBoard().size();
            powerToAdd *= boardSize - 1;
        }

        Set<CardInstance> availableCards = new HashSet<>();

        if (self) {
            availableCards.add(effectSource);
        }

        if (allies) {
            currentPlayer.getBoard().stream().filter(currentCard -> !currentCard.equals(effectSource)).forEach(availableCards::add);
        }

        changePower(game, availableCards, powerToAdd, timing);
    }

    private void changePower(Game game, Collection<CardInstance> cards, int power, EffectTiming timing) {
        for (CardInstance card : cards) {
            card.changePower(power);
            if (timing == EffectTiming.ATTACK) {
                card.getModifiers().add(new PowerModifier(power));
            }
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
