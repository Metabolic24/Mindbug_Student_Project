package org.metacorp.mindbug.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.player.Player;

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
    private Set<Keyword> keywords;

    private boolean stillTough;
    private boolean canAttackTwice;
    private boolean canAttack;
    private boolean canBlock;

    public CardInstance(Card card) {
        this.uuid = UUID.randomUUID();
        this.card = card;
        this.power = card.getPower();
        this.keywords = new HashSet<>(card.getKeywords());

        this.stillTough = this.keywords.contains(Keyword.TOUGH);
        this.canAttackTwice = this.keywords.contains(Keyword.FRENZY);
        this.canAttack = true;
        this.canBlock = true;
    }

    public List<AbstractEffect> getEffects(EffectTiming timing) {
        List<AbstractEffect> effects = this.card.getEffects().get(timing);
        return effects == null ? new ArrayList<>() : effects;
    }

    /**
     * Checks whether this card has the given keyword
     */
    public boolean hasKeyword(Keyword keyword) {
        return this.keywords.contains(keyword);
    }

    /**
     * Change the power of the current card by the given amount (positive or negative)
     */
    public void changePower(int amount) {
        power += amount;
    }

    public void reset() {
        power = card.getPower();
        canAttack = true;
        canBlock = true;
        keywords = new HashSet<>(card.getKeywords());
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
