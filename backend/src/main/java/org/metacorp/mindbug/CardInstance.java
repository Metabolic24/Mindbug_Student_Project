package org.metacorp.mindbug;

import lombok.*;

import java.util.*;

/** Class that describes an instance of a card */
@Getter
@Setter
@ToString
public class CardInstance {
    /** UUID of a card instance (used by equals method)*/
    private final UUID uuid;

    private Card card;
    private Player owner;

    private int power;
    private Set<Keyword> keywords;
    private boolean stillTough;
    private boolean canAttack;
    private boolean canBlock;

    public CardInstance(Card card) {
        this.uuid = UUID.randomUUID();
        this.card = card;
        this.power = card.getPower();
        this.keywords = new HashSet<>(card.getKeywords());
        this.stillTough = this.keywords.contains(Keyword.TOUGH);
        this.canAttack = true;
        this.canBlock = true;
    }

    public List<Effect> getEffects(EffectTiming timing) {
        List<Effect> effects = this.card.getEffects().get(timing);
        return effects == null ? new ArrayList<>() : effects;
    }

    public boolean hasEffects(EffectTiming timing) {
        return this.card.getEffects().containsKey(timing);
    }

    public void gainPower(int amount) {
        power += amount;
    }

    public void losePower(int amount) {
        power -= amount;
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
