package org.metacorp.mindbug.card.effect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EffectToApply {
    @NonNull
    private AbstractEffect effect;
    private CardInstance card;
    private Game game;

}
