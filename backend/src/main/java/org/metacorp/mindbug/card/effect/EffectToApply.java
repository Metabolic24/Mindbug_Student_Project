package org.metacorp.mindbug.card.effect;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;

import java.util.UUID;

@Getter
@Setter
public class EffectToApply {
    private UUID uuid;
    @NonNull
    private AbstractEffect effect;
    private CardInstance card;
    private Game game; //TODO Voir si on a vraiment besoin de ça

    /**
     * @param effect the effect that will later be applied
     * @param card the card related to the effect
     * @param game the current game instance
     */
    public EffectToApply(AbstractEffect effect, CardInstance card, Game game) {
        uuid = UUID.randomUUID();
        this.effect = effect;
        this.card = card;
        this.game = game;
    }
}
