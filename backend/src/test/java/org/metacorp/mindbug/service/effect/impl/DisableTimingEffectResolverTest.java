package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class DisableTimingEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private DisableTimingEffect effect;
    private DisableTimingEffectResolver effectResolver;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player("Player1"), new Player("Player2"));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new DisableTimingEffect();
        effectResolver = new DisableTimingEffectResolver(effect);
    }

    @Test
    public void testBasic_PLAY() {
        effect.setValue(EffectTiming.PLAY);
        effectResolver.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));

    }

    @Test
    public void testBasic_ATTACK() {
        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_DEFEATED() {
        effect.setValue(EffectTiming.DEFEATED);
        effectResolver.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_multiple() {
        effect.setValue(EffectTiming.DEFEATED);
        effectResolver.apply(game, randomCard);

        effect.setValue(EffectTiming.PLAY);
        effectResolver.apply(game, randomCard);

        assertEquals(2, opponentPlayer.getDisabledTiming().size());
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_sameTwice() {
        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, randomCard);

        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }
}
