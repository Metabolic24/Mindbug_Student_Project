package org.metacorp.mindbug.model.card;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.modifier.AbstractModifier;
import org.metacorp.mindbug.model.modifier.KeywordModifier;
import org.metacorp.mindbug.model.modifier.PowerModifier;
import org.metacorp.mindbug.model.player.Player;

import java.util.*;

/**
 * Class that describes an instance of a card
 */
@Getter
@Setter
@ToString
public class CardInstance {
    /**
     * UUID of a card instance (used by equals method)
     */
    private final UUID uuid;

    private Card card;
    private Player owner;

    private int power;
    private Set<CardKeyword> keywords;
    private boolean stillTough;
    private boolean ableToAttackTwice;

    private boolean ableToAttack;
    private boolean ableToBlock;

    @Getter(AccessLevel.PRIVATE)
    private boolean protection;

    private Set<AbstractModifier<?>> modifiers;

    public CardInstance(Card card) {
        this.uuid = UUID.randomUUID();
        this.card = card;
        this.power = card.getPower();
        this.keywords = new HashSet<>(card.getKeywords());

        this.stillTough = this.keywords.contains(CardKeyword.TOUGH);
        this.ableToAttackTwice = this.keywords.contains(CardKeyword.FRENZY);
        this.ableToAttack = true;
        this.ableToBlock = true;
        this.modifiers = new HashSet<>();
    }

    public List<GenericEffect> getEffects(EffectTiming timing) {
        List<GenericEffect> effects = this.card.getEffects().get(timing);
        return effects == null ? new ArrayList<>() : effects;
    }

    /**
     * Checks whether this card has the given keyword
     */
    public boolean hasKeyword(CardKeyword keyword) {
        return this.keywords.contains(keyword);
    }

    /**
     * Change the power of the current card by the given amount (positive or negative)
     */
    public void changePower(int amount) {
        power += amount;
    }

    public void reset(boolean afterAttack) {
        power = card.getPower();
        ableToAttack = true;
        ableToBlock = true;
        keywords = new HashSet<>(card.getKeywords());

        // Apply or clear modifiers depending on the current step
        if (afterAttack) {
            modifiers.clear();
        } else {
            modifiers.forEach(modifier -> modifier.apply(this));
        }
    }

    /**
     * @return true if the card has a protection, false otherwise
     */
    public boolean hasProtection() {
        return protection;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CardInstance that = (CardInstance) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
