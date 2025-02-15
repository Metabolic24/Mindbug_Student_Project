package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class PlayCardServiceTest {

    private Game game;
    private Player currentPlayer;
    private Player opponent;

    @BeforeEach
    public void initGame() {
        game = StartService.newGame("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        opponent = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testManagePlayedCard_nominal() {
        CardInstance card = currentPlayer.getHand().getFirst();
        game.setPlayedCard(card);

        PlayCardService.managePlayedCard(currentPlayer, game);

        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(card, currentPlayer.getBoard().getFirst());
        assertTrue(opponent.getBoard().isEmpty());

        assertEquals(card.getEffects(EffectTiming.PLAY).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testManagePlayedCard_mindbug() {
        CardInstance card = currentPlayer.getHand().getFirst();
        game.setPlayedCard(card);

        PlayCardService.managePlayedCard(opponent, game);

        assertTrue(currentPlayer.getBoard().isEmpty());
        assertEquals(1, opponent.getBoard().size());
        assertEquals(card, opponent.getBoard().getFirst());

        assertEquals(opponent, card.getOwner());
        assertEquals(1, opponent.getMindBugs());

        assertEquals(card.getEffects(EffectTiming.PLAY).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testManagePlayedCard_playRestricted() {
        currentPlayer.disableTiming(EffectTiming.PLAY);

        CardInstance card = currentPlayer.getHand().getFirst();
        game.setPlayedCard(card);

        PlayCardService.managePlayedCard(currentPlayer, game);

        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(card, currentPlayer.getBoard().getFirst());
        assertTrue(opponent.getBoard().isEmpty());

        assertEquals(0, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }
}
