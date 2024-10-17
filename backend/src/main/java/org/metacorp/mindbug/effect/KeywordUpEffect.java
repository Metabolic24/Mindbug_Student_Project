package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;

/** Effect that adds a keyword to one or more cards */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class KeywordUpEffect extends Effect {
    public final static String TYPE = "KEYWORD_UP";

    private Keyword value;          // The keyword that should be added to the card(s)
    private Integer max;            // The maximum power of card(s) on which this effect should be applied
    private boolean alone;          // Should this effect be applied only if current card is alone
    private boolean moreAllies;     // Should this effect be applied only if card owner has more allies than the opponent
    private boolean opponentHas;    // Should this effect be applied only if the opponent has a card that contains this keyword

    @Override
    public String getType() {
        return TYPE;
    }
}
