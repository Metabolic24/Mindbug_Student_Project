package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.effect.impl.StealEffect;
import org.metacorp.mindbug.model.effect.steal.StealSource;
import org.metacorp.mindbug.model.effect.steal.StealTargetSelection;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.StartService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EffectQueueServiceTest {

    private CardInstance card;
    private Game game;

    @BeforeEach
    public void setUp() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));

        Player currentPlayer = game.getCurrentPlayer();
        card = currentPlayer.getHand().getFirst();

        currentPlayer.addCardToBoard(card);
    }

    @Test
    public void testAddEffectsToQueue_nominal() {
        // Remove ATTACK effects from the card
        if (card.getEffects(EffectTiming.ATTACK).isEmpty()) {
            card.getCard().getEffects().put(EffectTiming.ATTACK, Collections.singletonList(new GainEffect()));
        }

        card.getOwner().disableTiming(EffectTiming.DEFEATED); // Just to check that it has no impact

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.ATTACK, game.getEffectQueue());

        if (card.getEffects(EffectTiming.ATTACK).isEmpty()) {
            assertTrue(game.getEffectQueue().isEmpty());
        } else {
            assertEquals(1, game.getEffectQueue().size());

            EffectsToApply effectsToApply = game.getEffectQueue().peek();
            assertNotNull(effectsToApply);
            assertEquals(card.getEffects(EffectTiming.ATTACK).size(), effectsToApply.getEffects().size());
        }
    }

    @Test
    public void testAddEffectsToQueue_empty() {
        // Remove PLAY effects from the card
        card.getEffects(EffectTiming.PLAY).clear();

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());

        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void testAddEffectsToQueue_disabled() {
        // Remove DEFEATED effects from the card
        if (card.getEffects(EffectTiming.DEFEATED).isEmpty()) {
            card.getCard().getEffects().put(EffectTiming.DEFEATED, Collections.singletonList(new GainEffect()));
        }

        card.getOwner().disableTiming(EffectTiming.DEFEATED);

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());

        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void testResolveEffectQueue_empty() throws GameStateException {
        EffectQueueService.resolveEffectQueue(false, game);

        assertNull(game.getChoice());
        assertNull(game.getAfterEffect());
        assertTrue(game.getEffectQueue().isEmpty());
        assertFalse(game.getEffectQueue().isResolvingEffect());
    }

    @Test
    public void testResolveEffectQueue_emptyWithAfterEffect() throws GameStateException {
        // Add an after effect that changes the current player
        Player currentPlayer = game.getCurrentPlayer();
        game.setAfterEffect(() -> game.setCurrentPlayer(game.getCurrentPlayer().getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(false, game);

        assertNull(game.getChoice());
        assertNull(game.getAfterEffect());
        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(currentPlayer.getOpponent(game.getPlayers()), game.getCurrentPlayer());
        assertFalse(game.getEffectQueue().isResolvingEffect());
    }

    @Test
    public void testResolveEffectQueue_choice() {
        game.setChoice(new FrenzyAttackChoice(card));

        try {
            EffectQueueService.resolveEffectQueue(false, game);
            fail("An exception should have been thrown");
        } catch (GameStateException e) {
            assertEquals("Inconsistent game state: a choice needs to be resolved before resolving effect queue", e.getMessage());
            assertEquals(e.getAdditionalData().get("choice"), game.getChoice());
        }
    }

    @Test
    public void testResolveEffectQueue_multiple() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = currentPlayer.getHand().getFirst();
            currentPlayer.addCardToBoard(otherCard);
        }

        otherCard.getCard().getEffects().put(EffectTiming.PLAY, Collections.singletonList(new GainEffect()));
        card.getCard().getEffects().put(EffectTiming.PLAY, Collections.singletonList(new InflictEffect()));

        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.PLAY, game.getEffectQueue());

        EffectQueueService.resolveEffectQueue(false, game);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNotNull(game.getAfterEffect());
        assertNotNull(game.getChoice());
        assertFalse(game.getEffectQueue().isResolvingEffect());

        SimultaneousEffectsChoice choice = (SimultaneousEffectsChoice) game.getChoice();
        assertEquals(ChoiceType.SIMULTANEOUS, choice.getType());
        assertEquals(2, choice.getEffectsToSort().size());

        for (EffectsToApply effect : choice.getEffectsToSort()) {
            assertEquals(1, effect.getEffects().size());
            assertTrue(effect.getCard().equals(card) && effect.getEffects().getFirst() instanceof InflictEffect ||
                    effect.getCard().equals(otherCard) && effect.getEffects().getFirst() instanceof GainEffect);
        }
    }

    @Test
    public void testResolveEffectQueue_nominalSingle() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        inflictEffect.setType(EffectType.INFLICT);
        card.getCard().getEffects().put(EffectTiming.ATTACK, new ArrayList<>(Collections.singletonList(inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.ATTACK, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(false, game);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNull(game.getAfterEffect());
        assertNull(game.getChoice());
        assertEquals(currentPlayer.getOpponent(game.getPlayers()), game.getCurrentPlayer());
        assertEquals(2, game.getCurrentPlayer().getTeam().getLifePoints());
        assertFalse(game.getEffectQueue().isResolvingEffect());
        assertEquals(5, currentPlayer.getHand().size());
    }

    @Test
    public void testResolveEffectQueue_twoEffectsAfterChoice() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = currentPlayer.getHand().getFirst();
            currentPlayer.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(1);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        inflictEffect.setType(EffectType.INFLICT);
        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(true, game);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNull(game.getAfterEffect());
        assertNull(game.getChoice());
        assertEquals(currentPlayer.getOpponent(game.getPlayers()), game.getCurrentPlayer());
        assertEquals(2, game.getCurrentPlayer().getTeam().getLifePoints());
        assertEquals(4, currentPlayer.getTeam().getLifePoints());
        assertFalse(game.getEffectQueue().isResolvingEffect());
        assertEquals(5, currentPlayer.getHand().size());
    }

    @Test
    public void testResolveEffectQueue_twoEffectsWhileResolvingEffect() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = currentPlayer.getHand().getFirst();
            currentPlayer.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(1);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        inflictEffect.setType(EffectType.INFLICT);
        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        game.getEffectQueue().setResolvingEffect(true);
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(false, game);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNull(game.getAfterEffect());
        assertNull(game.getChoice());
        assertEquals(currentPlayer.getOpponent(game.getPlayers()), game.getCurrentPlayer());
        assertEquals(2, game.getCurrentPlayer().getTeam().getLifePoints());
        assertEquals(4, currentPlayer.getTeam().getLifePoints());

        assertFalse(game.getEffectQueue().isResolvingEffect());
        assertEquals(5, currentPlayer.getHand().size());
    }

    @Test
    public void testResolveEffectQueue_twoEffectsButFirstEndsGame() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        CardInstance otherCard = opponent.getHand().getFirst();
        opponent.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = opponent.getHand().getFirst();
            opponent.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(1);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(3);
        inflictEffect.setType(EffectType.INFLICT);
        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(true, game);

        assertFalse(game.getEffectQueue().isEmpty());
        assertNotNull(game.getAfterEffect());
        assertNull(game.getChoice());
        assertEquals(currentPlayer, game.getCurrentPlayer());
        assertEquals(0, opponent.getTeam().getLifePoints());
        assertEquals(3, currentPlayer.getTeam().getLifePoints());
        assertTrue(game.isFinished());
        assertFalse(game.getEffectQueue().isResolvingEffect());
        assertEquals(5, opponent.getHand().size());
        assertEquals(5, currentPlayer.getHand().size());
    }

    @Test
    public void testResolveEffectQueue_fourEffectsWithDoubleOne() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        CardInstance otherCard = opponent.getHand().getFirst();
        opponent.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = opponent.getHand().getFirst();
            opponent.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(3);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));


        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard2);

        // In case the card are similar, there would be a problem as we modify the card effects
        while (otherCard2.getCard().equals(card.getCard()) || otherCard2.getCard().equals(otherCard.getCard())) {
            otherCard2 = currentPlayer.getHand().getFirst();
            currentPlayer.addCardToBoard(otherCard2);
        }

        gainEffect = new GainEffect();
        gainEffect.setValue(1);
        gainEffect.setType(EffectType.GAIN);
        otherCard2.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        CardInstance otherCard3 = opponent.getHand().getFirst();
        opponent.addCardToBoard(otherCard3);

        // In case the card are similar, there would be a problem as we modify the card effects
        while (otherCard3.getCard().equals(card.getCard()) || otherCard3.getCard().equals(otherCard.getCard()) || otherCard3.getCard().equals(otherCard2.getCard())) {
            otherCard3 = opponent.getHand().getFirst();
            opponent.addCardToBoard(otherCard3);
        }

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(2);
        inflictEffect.setType(EffectType.GAIN);
        otherCard3.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(inflictEffect)));

        gainEffect = new GainEffect();
        gainEffect.setValue(1);
        gainEffect.setType(EffectType.GAIN);

        inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        inflictEffect.setType(EffectType.INFLICT);

        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Arrays.asList(gainEffect, inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard2, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard3, EffectTiming.DEFEATED, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        EffectQueueService.resolveEffectQueue(true, game);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNotNull(game.getAfterEffect());
        assertNotNull(game.getChoice());
        assertEquals(currentPlayer, game.getCurrentPlayer());
        assertEquals(2, opponent.getTeam().getLifePoints());
        assertEquals(4, currentPlayer.getTeam().getLifePoints());
        assertFalse(game.getEffectQueue().isResolvingEffect());

        SimultaneousEffectsChoice choice = (SimultaneousEffectsChoice) game.getChoice();
        assertEquals(ChoiceType.SIMULTANEOUS, choice.getType());
        assertEquals(3, choice.getEffectsToSort().size());

        for (EffectsToApply effect : choice.getEffectsToSort()) {
            assertEquals(1, effect.getEffects().size());
            assertTrue(effect.getCard().equals(otherCard) && effect.getEffects().getFirst() instanceof GainEffect ||
                    effect.getCard().equals(otherCard2) && effect.getEffects().getFirst() instanceof GainEffect ||
                    effect.getCard().equals(otherCard3) && effect.getEffects().getFirst() instanceof InflictEffect);
        }
    }

    @Test
    public void testResolveEffectQueue_twoEffectsWithDoubleOneAndChoice() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        CardInstance otherCard = opponent.getHand().getFirst();
        opponent.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = opponent.getHand().getFirst();
            opponent.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(3);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        StealEffect stealEffect = new StealEffect();
        stealEffect.setValue(1);
        stealEffect.setType(EffectType.STEAL);
        stealEffect.setSource(StealSource.HAND);
        stealEffect.setSelection(StealTargetSelection.OPPONENT);
        stealEffect.setMayPlay(true);

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        inflictEffect.setType(EffectType.INFLICT);

        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Arrays.asList(stealEffect, inflictEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        int handSize = opponent.getHand().size();

        EffectQueueService.resolveEffectQueue(true, game);

        assertEquals(2, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
        assertNotNull(game.getChoice());
        assertEquals(currentPlayer, game.getCurrentPlayer());
        assertEquals(3, opponent.getTeam().getLifePoints());
        assertEquals(3, currentPlayer.getTeam().getLifePoints());
        assertTrue(game.getEffectQueue().isResolvingEffect());

        assertEquals(5, currentPlayer.getHand().size());
        assertEquals(5, opponent.getHand().size());

        EffectsToApply effectsToApply = game.getEffectQueue().peek();
        assertNotNull(effectsToApply);
        assertEquals(1, effectsToApply.getEffects().size());

        TargetChoice choice = (TargetChoice) game.getChoice();
        assertEquals(ChoiceType.TARGET, choice.getType());
        assertEquals(handSize, choice.getAvailableTargets().size());
    }

    @Test
    public void testResolveEffectQueue_twoEffectsWithChoice() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        CardInstance otherCard = opponent.getHand().getFirst();
        opponent.addCardToBoard(otherCard);

        // In case the card are similar, there would be a problem as we modify the card effects
        if (otherCard.getCard().equals(card.getCard())) {
            otherCard = opponent.getHand().getFirst();
            opponent.addCardToBoard(otherCard);
        }

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(3);
        gainEffect.setType(EffectType.GAIN);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(Collections.singletonList(gainEffect)));

        StealEffect stealEffect = new StealEffect();
        stealEffect.setValue(1);
        stealEffect.setType(EffectType.STEAL);
        stealEffect.setSource(StealSource.HAND);
        stealEffect.setSelection(StealTargetSelection.OPPONENT);
        stealEffect.setMayPlay(true);

        card.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(List.of(stealEffect)));

        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, game.getEffectQueue());
        EffectQueueService.addBoardEffectsToQueue(otherCard, EffectTiming.DEFEATED, game.getEffectQueue());
        game.setAfterEffect(() -> game.setCurrentPlayer(currentPlayer.getOpponent(game.getPlayers())));

        int handSize = opponent.getHand().size();

        EffectQueueService.resolveEffectQueue(true, game);

        assertEquals(1, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
        assertNotNull(game.getChoice());
        assertEquals(currentPlayer, game.getCurrentPlayer());
        assertEquals(3, opponent.getTeam().getLifePoints());
        assertEquals(3, currentPlayer.getTeam().getLifePoints());
        assertFalse(game.getEffectQueue().isResolvingEffect());

        assertEquals(5, currentPlayer.getHand().size());
        assertEquals(5, opponent.getHand().size());

        TargetChoice choice = (TargetChoice) game.getChoice();
        assertEquals(ChoiceType.TARGET, choice.getType());
        assertEquals(handSize, choice.getAvailableTargets().size());
    }
}
