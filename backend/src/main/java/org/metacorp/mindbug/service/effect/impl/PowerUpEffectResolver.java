package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;
import org.metacorp.mindbug.model.modifier.PowerModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

/**
 * Effect resolver for PowerUpEffect
 */
public class PowerUpEffectResolver extends GenericEffectResolver<PowerUpEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public PowerUpEffectResolver(PowerUpEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        int value = effect.getValue();
        boolean alone = effect.isAlone();
        boolean self = effect.isSelf();
        boolean selfTurn = effect.isSelfTurn();
        boolean allies = effect.isAllies();
        boolean forEachAlly = effect.isForEachAlly();
        Integer lifePoints = effect.getLifePoints();
        Integer enemiesCount = effect.getEnemiesCount();

        Player currentPlayer = card.getOwner();
        int powerToAdd = value;

        if ((lifePoints != null && currentPlayer.getTeam().getLifePoints() > lifePoints) ||
                (enemiesCount != null && game.getOpponent().getBoard().size() < enemiesCount) ||
                (alone && currentPlayer.getBoard().size() != 1) ||
                (selfTurn && !currentPlayer.equals(game.getCurrentPlayer()))) {
            return;
        }

        if (forEachAlly) {
            int boardSize = currentPlayer.getBoard().size();
            powerToAdd *= boardSize - 1;
        }

        if (self) {
            changePower(card, powerToAdd, timing);
        }

        if (allies) {
            for (CardInstance currentCard : currentPlayer.getBoard()) {
                if (!(currentCard.equals(card))) {
                    changePower(currentCard, powerToAdd, timing);
                }
            }
        }
    }

    private void changePower(CardInstance card, int power, EffectTiming timing) {
        card.changePower(power);
        if (timing == EffectTiming.ATTACK) {
            card.getModifiers().add(new PowerModifier(power));
        }
    }
}
