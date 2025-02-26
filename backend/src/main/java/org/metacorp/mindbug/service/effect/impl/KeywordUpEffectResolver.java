package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.impl.KeywordUpEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.AbstractEffectResolver;

/**
 * Effect resolver for KeywordUpEffect
 */
public class KeywordUpEffectResolver extends AbstractEffectResolver<KeywordUpEffect> {

    /**
     * Constructor
     * @param effect the effect to be resolved
     */
    public KeywordUpEffectResolver(KeywordUpEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card) {
        CardKeyword value = effect.getValue();
        Integer max = effect.getMax();
        boolean moreAllies = effect.isMoreAllies();
        boolean alone = effect.isAlone();
        boolean opponentHas = effect.isOpponentHas(); //TODO Problème si deux ont le même effet "miroir" ; risque que l'un ne copie pas l'autre suivant l'ordre d'exécution

        Player cardOwner = card.getOwner();

        if (alone && cardOwner.getBoard().size() != 1) {
            return;
        }

        Player opponent =  cardOwner.getOpponent(game.getPlayers());
        if (moreAllies && opponent.getBoard().size() >= cardOwner.getBoard().size()) {
            return;
        }

        if (opponentHas) {
            boolean checkOpponent = false;
            for (CardInstance opponentCard : opponent.getBoard()) {
                if (opponentCard.getKeywords().contains(value)) {
                    checkOpponent = true;
                    break;
                }
            }

            if (!checkOpponent) {
                return;
            }
        }

        if (max != null) {
            for (CardInstance currentCard : cardOwner.getBoard()) {
                if (currentCard.getPower() <= max && !currentCard.equals(card)) {
                    currentCard.getKeywords().add(value);
                    if (value == CardKeyword.FRENZY) {
                        currentCard.setAbleToAttackTwice(true);
                    } else if (value == CardKeyword.TOUGH) {
                        currentCard.setStillTough(true);
                    }
                }
            }
        } else {
            card.getKeywords().add(value);
        }
    }
}