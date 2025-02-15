package org.metacorp.mindbug.card.effect;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EffectToApply {
    private UUID uuid;
    @NonNull
    private List<AbstractEffect> effects;
    private CardInstance card;
    private Game game; //TODO Voir si on a vraiment besoin de ça

    /**
     * @param effects the effect that will later be applied
     * @param card the card related to the effect
     * @param game the current game instance
     */
    public EffectToApply(List<AbstractEffect> effects, CardInstance card, Game game) {
        uuid = card.getUuid();
        this.effects = effects;
        this.card = card;
        this.game = game;
    }
}
