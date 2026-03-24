package org.metacorp.mindbug.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BlockChoice;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.MindbugChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PlayCardServiceTest extends MindbugGameTest {

    private Game game;
    private Player currentPlayer;
    private Player opponent;

    @BeforeEach
    public void initGame() throws CardSetException {
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));

        currentPlayer = game.getCurrentPlayer();
        opponent = game.getOpponents().getFirst();
    }

    @Test
    public void testManagePlayedCard_nominal() throws WebSocketException, GameStateException {
        CardInstance card = currentPlayer.getHand().getFirst();

        PlayCardService.managePlayedCard(card, null, game);

        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(card, currentPlayer.getBoard().getFirst());
        assertTrue(opponent.getBoard().isEmpty());

        assertEquals(card.getEffects(EffectTiming.PLAY).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testManagePlayedCard_mindbug() throws WebSocketException, GameStateException {
        CardInstance card = currentPlayer.getHand().getFirst();

        PlayCardService.managePlayedCard(card, opponent, game);

        assertTrue(currentPlayer.getBoard().isEmpty());
        assertEquals(1, opponent.getBoard().size());
        assertEquals(card, opponent.getBoard().getFirst());

        assertEquals(opponent, card.getOwner());
        assertEquals(1, opponent.getMindBugs());

        assertEquals(card.getEffects(EffectTiming.PLAY).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testManagePlayedCard_playRestricted() throws WebSocketException, GameStateException {
        DisableTimingEffect effect = new DisableTimingEffect();
        effect.setValue(EffectTiming.PLAY);
        effect.setType(EffectType.DISABLE_TIMING);

        CardInstance opponentCard = opponent.getHand().getFirst();
        opponentCard.getCard().getEffects().put(EffectTiming.PASSIVE, new ArrayList<>(List.of(effect)));
        opponent.addCardToBoard(opponentCard);

        currentPlayer.disableTiming(EffectTiming.PLAY);

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(3);
        gainEffect.setType(EffectType.GAIN);

        CardInstance card = currentPlayer.getHand().getFirst();
        card.getCard().getEffects().put(EffectTiming.PLAY, new ArrayList<>(List.of(gainEffect)));

        PlayCardService.managePlayedCard(card, null, game);

        assertEquals(1, currentPlayer.getBoard().size());
        assertTrue(currentPlayer.getBoard().contains(card));
        assertEquals(1, opponent.getBoard().size());

        assertEquals(0, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());

        assertFalse(currentPlayer.canTrigger(EffectTiming.PLAY));
    }

    @Test
    public void testPickCard_nominal() throws GameStateException, WebSocketException {
        CardInstance card = currentPlayer.getHand().getFirst();

        PlayCardService.pickCard(card, game);
        assertEquals(5, currentPlayer.getHand().size());
        assertFalse(currentPlayer.getHand().contains(card));
        assertEquals(4, currentPlayer.getDrawPile().size());
        assertTrue(currentPlayer.getBoard().isEmpty());

        assertNotNull(game.getChoice());
        MindbugChoice choice = assertInstanceOf(MindbugChoice.class, game.getChoice());
        assertEquals(card, choice.getPlayedCard());
        assertEquals(opponent, choice.getPlayerToChoose());
        assertTrue(choice.getRemainingPlayers().isEmpty());
    }

    @Test
    public void testPickCard_playedCard() throws WebSocketException {
        CardInstance card = currentPlayer.getHand().getFirst();
        game.setChoice(new MindbugChoice(card, Collections.singletonList(opponent)));

        try {
            PlayCardService.pickCard(card, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: a choice needs to be resolved before picking a new card", e.getMessage());
            assertEquals(e.getAdditionalData().get("choice"), game.getChoice());
        }
    }

    @Test
    public void testPickCard_choice() throws WebSocketException {
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
    public void testPickCard_attackingCard() throws WebSocketException {
        CardInstance card = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(card);
        game.setChoice(new BlockChoice(card, Collections.singletonList(currentPlayer), new HashMap<>()));

        CardInstance otherCard = currentPlayer.getHand().getFirst();

        try {
            PlayCardService.pickCard(otherCard, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: a choice needs to be resolved before picking a new card", e.getMessage());
            assertEquals(e.getAdditionalData().get("choice"), game.getChoice());
        }
    }
}
