package org.metacorp.mindbug.card;

import lombok.Data;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;

import java.util.*;

/** Describes a Mindbug card (out of a game)*/
@Data
public class Card {
    private String name;
    private int power;
    private Set<Keyword> keywords;
    private Map<EffectTiming, List<AbstractEffect>> effects;
    private boolean unique; // Has this card multiple copies in the set or not

    /**
     * Empty constructor (required by Jackson)
     */
    public Card() {
        this.keywords = new HashSet<>();
        this.effects = new HashMap<>();
    }
}
