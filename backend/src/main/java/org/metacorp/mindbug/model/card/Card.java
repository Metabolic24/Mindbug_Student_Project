package org.metacorp.mindbug.model.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Card {
    private int id;
    @JsonIgnore
    private String setName;
    private String name;
    private int power;
    private Set<CardKeyword> keywords;
    private Map<EffectTiming, List<Effect>> effects;
    private boolean unique;     // Has this card multiple copies in the set or not
    private Integer initialCardId;  // Filled if this card is an evolution of another card

    /**
     * Empty constructor (required by Jackson)
     */
    public Card() {
        this.keywords = new HashSet<>();
        this.effects = new HashMap<>();
    }

    /**
     * Copy constructor (to be used when creating custom card set)
     *
     * @param otherCard the card to copy
     */
    public Card(Card otherCard) {
        this.id = otherCard.id;
        this.name = otherCard.name;
        this.power = otherCard.power;
        this.keywords = new HashSet<>(otherCard.keywords);
        this.effects = new HashMap<>(otherCard.effects);
        this.initialCardId = otherCard.initialCardId;

        this.unique = true; // By default, we set it to true
    }
}
