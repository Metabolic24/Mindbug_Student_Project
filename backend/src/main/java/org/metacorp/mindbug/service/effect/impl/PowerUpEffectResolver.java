package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;
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
    public void apply(Game game, CardInstance card) {
        int value = effect.getValue();
        boolean alone = effect.isAlone();
        boolean self = effect.isSelf();
        boolean selfTurn = effect.isSelfTurn();
        boolean allies = effect.isAllies();
        boolean forEachAlly = effect.isForEachAlly();
        Integer lifePoints = effect.getLifePoints();

        Player currentPlayer = card.getOwner();
        int powerToAdd = value;

        if ((lifePoints != null && currentPlayer.getTeam().getLifePoints() > lifePoints) ||
                (alone && currentPlayer.getBoard().size() != 1) ||
                (selfTurn && !currentPlayer.equals(game.getCurrentPlayer()))) {
            return;
        }

        if (forEachAlly) {
            int boardSize = currentPlayer.getBoard().size();
            powerToAdd *= boardSize - 1;
        }

        if (self) {
            card.changePower(powerToAdd);
        }

        if (allies) {
            for (CardInstance currentCard : currentPlayer.getBoard()) {
                if (!(currentCard.equals(card))) {
                    currentCard.changePower(powerToAdd);
                }
            }
        }
    }
}
