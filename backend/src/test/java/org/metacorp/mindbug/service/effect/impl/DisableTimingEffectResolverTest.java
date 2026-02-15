package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.utils.MindbugGameTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DisableTimingEffectResolverTest extends MindbugGameTest {

    private Game game;
    private Player opponentPlayer;

    private DisableTimingEffect effect;
    private DisableTimingEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        CardInstance randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new DisableTimingEffect();
        effect.setType(EffectType.DISABLE_TIMING);
        effectResolver = new DisableTimingEffectResolver(effect, randomCard);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic_PLAY() {
        effect.setValue(EffectTiming.PLAY);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));

    }

    @Test
    public void testBasic_ATTACK() {
        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_DEFEATED() {
        effect.setValue(EffectTiming.DEFEATED);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_multiple() {
        effect.setValue(EffectTiming.DEFEATED);
        effectResolver.apply(game, timing);

        effect.setValue(EffectTiming.PLAY);
        effectResolver.apply(game, timing);

        assertEquals(2, opponentPlayer.getDisabledTiming().size());
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }

    @Test
    public void testBasic_sameTwice() {
        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, timing);

        effect.setValue(EffectTiming.ATTACK);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getDisabledTiming().size());
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.PLAY));
        assertTrue(opponentPlayer.getDisabledTiming().contains(EffectTiming.ATTACK));
        assertFalse(opponentPlayer.getDisabledTiming().contains(EffectTiming.DEFEATED));
    }
}
