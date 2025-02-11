package org.metacorp.mindbug.card.effect.keywordUp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.player.Player;

/** Effect that adds a keyword to one or more cards */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class KeywordUpEffect extends AbstractEffect {
    public final static String TYPE = "KEYWORD_UP";

    private Keyword value;          // The keyword that should be added to the card(s)
    private Integer max;            // The maximum power of ally card(s) on which this effect should be applied
    private boolean alone;          // Should this effect be applied only if current card is alone
    private boolean moreAllies;     // Should this effect be applied only if card owner has more allies than the opponent
    private boolean opponentHas;    // Should this effect be applied only if the opponent has a card that contains this keyword

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
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
                    if (value == Keyword.FRENZY) {
                        currentCard.setCanAttackTwice(true);
                    } else if (value == Keyword.TOUGH) {
                        currentCard.setStillTough(true);
                    }
                }
            }
        } else {
            card.getKeywords().add(value);
        }
    }
}
