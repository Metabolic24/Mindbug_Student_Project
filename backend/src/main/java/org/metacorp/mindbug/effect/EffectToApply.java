package org.metacorp.mindbug.effect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EffectToApply {
    @NonNull
    private Effect effect;
    private CardInstance card;
    private Game game;

}
