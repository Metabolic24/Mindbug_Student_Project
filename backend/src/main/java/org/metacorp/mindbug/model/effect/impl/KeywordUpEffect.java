package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that adds a keyword to one or more cards
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class KeywordUpEffect extends GenericEffect {
    public static final String TYPE = "KEYWORD_UP";

    /**
     * The keyword that should be added to the card(s)
     */
    private CardKeyword value;
    /**
     * Should this effect apply to the card itself
     */
    private boolean self = true;
    /**
     * Should this effect apply to the card allies
     */
    private boolean allies;
    /**
     * The maximum power of ally card(s) on which this effect should be applied
     */
    private Integer max;
    /**
     * Should this effect be applied only if current card is alone
     */
    private boolean alone;
    /**
     * Should this effect be applied only if card owner has more allies than the opponent
     */
    private boolean moreAllies;
    /**
     * Should this effect be applied only if the opponent has a card that contains this keyword
     */
    private boolean opponentHas;
    /**
     * Should power be gained only if the opponent has X or more cards on board
     */
    private Integer alliesCount;

    @Override
    public int getPriority() {
        // If opponentHas is true, it must be resolved after the others
        return opponentHas ? 2 : 1;
    }
}
