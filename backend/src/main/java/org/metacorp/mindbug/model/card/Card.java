package org.metacorp.mindbug.model.card;

import lombok.Data;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes a Mindbug card (out of a game)
 */
@Data
public class Card {
    private int id;
    private String setName;
    private String name;
    private int power;
    private Set<CardKeyword> keywords;
    private Map<EffectTiming, List<Effect>> effects;
    private boolean unique;     // Has this card multiple copies in the set or not
    private boolean evolution;  // Is this card an evolution of another card

    /**
     * Empty constructor (required by Jackson)
     */
    public Card() {
        this.keywords = new HashSet<>();
        this.effects = new HashMap<>();
    }
}
