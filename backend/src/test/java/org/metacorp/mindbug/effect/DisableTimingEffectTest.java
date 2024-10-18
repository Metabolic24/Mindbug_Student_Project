package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.EffectTiming;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DisableTimingEffectTest {

    @Test
    public void testApply() {
        Game game = new Game("Player1", "Player2");

        CardInstance cardInstance = game.getCurrentPlayer().getHand().getFirst();

        DisableTimingEffect effect = new DisableTimingEffect();
        effect.setValue(EffectTiming.PLAY);

        effect.apply(game, cardInstance);

        Player opponent = game.getCurrentPlayer().getOpponent(game.getPlayers());

        assertEquals(1, opponent.getDisabledTiming().size());
        assertTrue(opponent.getDisabledTiming().contains(EffectTiming.PLAY));
    }
}
