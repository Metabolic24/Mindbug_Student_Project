package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.KeywordUpEffect;
import org.metacorp.mindbug.model.modifier.KeywordModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

/**
 * Effect resolver for KeywordUpEffect
 */
public class KeywordUpEffectResolver extends GenericEffectResolver<KeywordUpEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public KeywordUpEffectResolver(KeywordUpEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        CardKeyword value = effect.getValue();
        Integer max = effect.getMax();
        boolean moreAllies = effect.isMoreAllies();
        boolean alone = effect.isAlone();
        Integer alliesCount = effect.getAlliesCount();

        Player cardOwner = card.getOwner();
        Player opponent = cardOwner.getOpponent(game.getPlayers());

        if ((alone && cardOwner.getBoard().size() != 1) ||
                (moreAllies && opponent.getBoard().size() >= cardOwner.getBoard().size()) ||
                (alliesCount != null && cardOwner.getBoard().size() != alliesCount)) {
            return;
        }

        if (effect.isOpponentHas()) { //TODO Fix an issue when there is at least one card with "opponentHas" effect on each side (one may not have all the expected keywords)
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
                    addKeyword(currentCard, value, timing);
                }
            }
        } else {
            addKeyword(card, value, timing);
        }
    }

    private void addKeyword(CardInstance card, CardKeyword keyword, EffectTiming timing) {
        if (!card.getKeywords().contains(keyword)) {
            card.getKeywords().add(keyword);
            if (keyword == CardKeyword.FRENZY) {
                card.setAbleToAttackTwice(true);
            } else if (keyword == CardKeyword.TOUGH) {
                card.setStillTough(true);
            }

            if (timing == EffectTiming.ATTACK) {
                card.getModifiers().add(new KeywordModifier(keyword));
            }
        }
    }
}