package org.metacorp.mindbug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

/** Describes a Mindbug card */
@Data
public class Card {
    private String name;
    private int power;
    private Set<Keyword> keywords;
    @JsonIgnore
    private Map<EffectTiming, List<Effect>> effects;
    private boolean unique; // Has this card multiple copies in the set or not

    /**
     * Empty constructor (required by Jackson)
     */
    public Card() {
        this.keywords = new HashSet<>();
        this.effects = new HashMap<>();
    }
}
