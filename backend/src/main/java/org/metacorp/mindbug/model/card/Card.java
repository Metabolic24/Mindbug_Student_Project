package org.metacorp.mindbug.model.card;

import lombok.Data;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.GenericEffect;

import java.util.*;

/**
 * Describes a Mindbug card (out of a game)
 */
@Data
public class Card {
    private String name;
    private int power;
    private Set<CardKeyword> keywords;
    private Map<EffectTiming, List<GenericEffect>> effects;
    private boolean unique; // Has this card multiple copies in the set or not

    /**
     * Empty constructor (required by Jackson)
     */
    public Card() {
        this.keywords = new HashSet<>();
        this.effects = new HashMap<>();
    }
}
