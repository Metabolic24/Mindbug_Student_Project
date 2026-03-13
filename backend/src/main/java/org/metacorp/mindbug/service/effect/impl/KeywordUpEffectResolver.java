package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.KeywordUpEffect;
import org.metacorp.mindbug.model.modifier.KeywordModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;

/**
 * Effect resolver for KeywordUpEffect
 */
public class KeywordUpEffectResolver extends EffectResolver<KeywordUpEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     
     */
    public KeywordUpEffectResolver(KeywordUpEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.effectSource = card;

        CardKeyword value = effect.getValue();
        boolean moreAllies = effect.isMoreAllies();
        boolean alone = effect.isAlone();
        boolean allies = effect.isAllies();
        Integer alliesCount = effect.getAlliesCount();

        Player cardOwner = card.getOwner();
        List<Player> opponentPlayers = cardOwner.getOpponents(game.getPlayers());
        int opponentBoardSize = opponentPlayers.stream().mapToInt(player -> player.getBoard().size()).sum();

        if ((alone && cardOwner.getBoard().size() != 1)
                || (moreAllies && opponentBoardSize >= cardOwner.getBoard().size())
                || (alliesCount != null && cardOwner.getBoard().size() != alliesCount)) {
            return;
        }

        if (effect.isOpponentHas()) { //TODO Fix an issue when there is at least one card with "opponentHas" effect on each side (one may not have all the expected keywords)
            boolean checkOpponent = opponentPlayers.stream()
                    .flatMap(player -> player.getBoard().stream())
                    .anyMatch(opponentCard -> opponentCard.getKeywords().contains(value));

            if (!checkOpponent) {
                return;
            }
        }

        if (allies) {
            Set<CardInstance> availableCards = cardOwner.getBoard().stream().filter(cardInstance ->
                    (effect.isSelf() && cardInstance.getUuid().equals(card.getUuid()))
                            || (!cardInstance.getUuid().equals(card.getUuid())
                            && (effect.getMax() == null || cardInstance.getPower() <= effect.getMax()))).collect(Collectors.toSet());

            addKeyword(game, availableCards, value, timing);
        } else {
            addKeyword(game, Collections.singleton(card), value, timing);
        }
    }

    private void addKeyword(Game game, Set<CardInstance> cards, CardKeyword keyword, EffectTiming timing) {
        for (CardInstance card : cards) {
            if (!card.getKeywords().contains(keyword)) {
                 game.getLogger().debug("{} gains {} keyword", getLoggableCard(card), keyword.name());
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

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
