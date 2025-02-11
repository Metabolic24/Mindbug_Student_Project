package org.metacorp.mindbug.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.card.effect.gainLp.GainEffect;
import org.metacorp.mindbug.choice.simultaneous.SimultaneousEffectsChoice;
import org.metacorp.mindbug.player.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private Player currentPlayer;
    private Player opponent;

    @BeforeEach
    public void initGame() {
        game = new Game("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        opponent = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testStart() {
        assertNotNull(currentPlayer);
        assertEquals(2, game.getPlayers().size());

        for (Player player : game.getPlayers()) {
            assertNotNull(player);
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertEquals(3, player.getTeam().getLifePoints());
            assertEquals(5, player.getHand().size());
            assertEquals(5, player.getDrawPile().size());
            assertTrue(player.getDiscardPile().isEmpty());
            assertTrue(player.getBoard().isEmpty());
            assertTrue(player.getDisabledTiming().isEmpty());
            assertEquals(2, player.getMindBugs());
        }

        assertTrue(game.getBannedCards().size() >= 2 && game.getBannedCards().size() % 2 == 0);
        assertTrue(!game.getCards().isEmpty() && game.getCards().size() <= 50);
        assertEquals(52, game.getCards().size() + game.getBannedCards().size());
    }

    @Test
    public void testManagePlayedCard_nominal() {
        CardInstance card = currentPlayer.getHand().getFirst();

        game.managePlayedCard(card, null);

        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(card, currentPlayer.getBoard().getFirst());
        assertTrue(opponent.getBoard().isEmpty());

        assertEquals(card.getEffects(EffectTiming.PLAY).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testManagePlayedCard_mindbug() {
        CardInstance card = currentPlayer.getHand().getFirst();

        game.managePlayedCard(card, opponent);

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

        game.managePlayedCard(card, null);

        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(card, currentPlayer.getBoard().getFirst());
        assertTrue(opponent.getBoard().isEmpty());

        assertEquals(0, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackDeclaration_nominal() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);

        game.processAttackDeclaration(attackCard);

        assertEquals(attackCard.getEffects(EffectTiming.ATTACK).size(), game.getEffectQueue().size());
    }

    @Test
    public void testProcessAttackDeclaration_attackRestricted() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);

        currentPlayer.disableTiming(EffectTiming.ATTACK);

        game.processAttackDeclaration(attackCard);

        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void testProcessAttackResolution_noBlock() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        game.processAttackResolution(attackCard, null);
        assertEquals(2, opponent.getTeam().getLifePoints());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_lowerBlocker() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() - 1);
        defendingCard.setStillTough(false);
        defendingCard.getKeywords().remove(Keyword.POISONOUS);
        opponent.addCardToBoard(defendingCard);

        game.processAttackResolution(attackCard, defendingCard);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertTrue(currentPlayer.getBoard().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_higherBlocker() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.setStillTough(false);
        attackCard.getKeywords().remove(Keyword.POISONOUS);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() + 1);
        opponent.addCardToBoard(defendingCard);

        game.processAttackResolution(attackCard, defendingCard);

        assertTrue(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertEquals(attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_samePowerBlockHasNoDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.setStillTough(false);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.getCard().getEffects().remove(EffectTiming.DEFEATED);
        defendingCard.setStillTough(false);
        opponent.addCardToBoard(defendingCard);

        game.processAttackResolution(attackCard, defendingCard);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttack_samePowerAttackHasNoDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getCard().getEffects().remove(EffectTiming.DEFEATED);
        attackCard.setStillTough(false);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.setStillTough(false);
        opponent.addCardToBoard(defendingCard);

        game.processAttackResolution(attackCard, defendingCard);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_samePowerBothHaveDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        attackCard.setStillTough(false);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        defendingCard.setStillTough(false);
        opponent.addCardToBoard(defendingCard);

        game.processAttackResolution(attackCard, defendingCard);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));

        assertEquals(2, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testResolveSimultaneousChoice() {
        GainEffect attackEffect = new GainEffect();
        CardInstance attackCard = currentPlayer.getHand().removeFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(attackEffect));
        currentPlayer.getDiscardPile().add(attackCard);

        GainEffect defendEffect = new GainEffect();
        CardInstance defendingCard = opponent.getHand().removeFirst();
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(defendEffect));
        opponent.getDiscardPile().add(defendingCard);

        SimultaneousEffectsChoice choice = new SimultaneousEffectsChoice(currentPlayer, new HashSet<>(Arrays.asList(
                new EffectToApply(attackEffect, attackCard, game),
                new EffectToApply(defendEffect, defendingCard, game)
        )));
        game.setCurrentChoice(choice);

        choice.resolve(game, choice.getEffectsToSort().stream().map(EffectToApply::getUuid).toList());

        assertNull(game.getCurrentChoice());
        assertEquals(2, game.getEffectQueue().size());

        for (EffectToApply effect : game.getEffectQueue()) {
            assertTrue(effect.getCard().equals(attackCard) || effect.getCard().equals(defendingCard));
        }
    }
}
