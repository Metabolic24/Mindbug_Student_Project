package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
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
        game = StartService.newGame(new Player("Player1"), new Player("Player2"));
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

    @Test
    public void testPickCard_nominal() throws GameStateException {
        CardInstance card = currentPlayer.getHand().getFirst();

        PlayCardService.pickCard(card, game);
        assertEquals(5, currentPlayer.getHand().size());
        assertFalse(currentPlayer.getHand().contains(card));
        assertEquals(4, currentPlayer.getDrawPile().size());
        assertTrue(currentPlayer.getBoard().isEmpty());

        assertEquals(card, game.getPlayedCard());
    }

    @Test
    public void testPickCard_playedCard() {
        CardInstance card = currentPlayer.getHand().getFirst();
        game.setPlayedCard(card);

        try {
            PlayCardService.pickCard(card, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: a card has already been picked", e.getMessage());
            assertEquals(e.getAdditionalData().get("playedCard"), game.getPlayedCard());
        }
    }

    @Test
    public void testPickCard_choice() {
        CardInstance card = currentPlayer.getHand().getFirst();
        game.setChoice(new FrenzyAttackChoice(card));

        try {
            PlayCardService.pickCard(card, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: a choice needs to be resolved before picking a new card", e.getMessage());
            assertEquals(e.getAdditionalData().get("choice"), game.getChoice());
        }
    }

    @Test
    public void testPickCard_attackingCard() {
        CardInstance card = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(card);
        game.setAttackingCard(card);

        CardInstance otherCard = currentPlayer.getHand().getFirst();

        try {
            PlayCardService.pickCard(otherCard, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: an attack needs to be resolved before picking a new card", e.getMessage());
            assertEquals(e.getAdditionalData().get("attackingCard"), card);
        }
    }
}
