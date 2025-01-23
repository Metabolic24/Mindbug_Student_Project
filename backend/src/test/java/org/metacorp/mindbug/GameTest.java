package org.metacorp.mindbug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.choice.Choice;
import org.metacorp.mindbug.choice.ChoiceLocation;
import org.metacorp.mindbug.choice.SimultaneousChoice;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.card.effect.gainLp.GainEffect;
import org.metacorp.mindbug.player.Player;

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

        assertTrue(game.getBannedCards().size() >= 2 && game.getBannedCards().size() %2 ==0);
        assertTrue(!game.getCards().isEmpty() && game.getCards().size() <= 50);
        assertEquals(52, game.getCards().size() + game.getBannedCards().size());
    }

    @Test
    public void testManagePlayedCard_nominal() {
        CardInstance card = currentPlayer.getHand().getFirst();

        game.managePlayedCard(card, false);

        assertEquals(card.getEffects(EffectTiming.PLAY).size() + 1, game.getEffectQueue().size());
    }

    @Test
    public void testManagePlayedCard_mindbug() {
        CardInstance card = currentPlayer.getHand().getFirst();

        game.managePlayedCard(card, true);

        assertEquals(card.getEffects(EffectTiming.PLAY).size() + 1, game.getEffectQueue().size());
    }

    @Test
    public void testManagePlayedCard_playRestricted() {
        currentPlayer.disableTiming(EffectTiming.PLAY);

        CardInstance card = currentPlayer.getHand().getFirst();

        game.managePlayedCard(card, false);

        assertEquals(1, game.getEffectQueue().size());
    }

    @Test
    public void testManageAttack_nominal() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        CardInstance defenseCard = opponent.getHand().getFirst();

        game.manageAttack(attackCard, defenseCard, opponent);

        assertEquals(attackCard.getEffects(EffectTiming.ATTACK).size() + 1, game.getEffectQueue().size());
    }

    @Test
    public void testManageAttack_attackRestricted() {
        currentPlayer.disableTiming(EffectTiming.ATTACK);

        CardInstance attackCard = currentPlayer.getHand().getFirst();
        CardInstance defenseCard = opponent.getHand().getFirst();

        game.manageAttack(attackCard, defenseCard, opponent);

        assertEquals(1, game.getEffectQueue().size());
    }

    @Test
    public void testResolveAttack_noBlock() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard, false);

        game.resolveAttack(attackCard, null, opponent);
        assertEquals(2, opponent.getTeam().getLifePoints());
    }

    @Test
    public void testResolveAttack_lowerBlocker() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard, false);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() - 1);
        opponent.addCardToBoard(defendingCard, false);

        game.resolveAttack(attackCard, defendingCard, opponent);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertTrue(currentPlayer.getBoard().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
    }

    @Test
    public void testResolveAttack_higherBlocker() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard, false);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() + 1);
        opponent.addCardToBoard(defendingCard, false);

        game.resolveAttack(attackCard, defendingCard, opponent);

        assertTrue(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertEquals(attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
    }

    @Test
    public void testResolveAttack_samePowerBlockHasNoDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard, false);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.getCard().getEffects().remove(EffectTiming.DEFEATED);
        opponent.addCardToBoard(defendingCard, false);

        game.resolveAttack(attackCard, defendingCard, opponent);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
    }

    @Test
    public void testResolveAttack_samePowerAttackHasNoDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getCard().getEffects().remove(EffectTiming.DEFEATED);
        currentPlayer.addCardToBoard(attackCard, false);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        opponent.addCardToBoard(defendingCard, false);

        game.resolveAttack(attackCard, defendingCard, opponent);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
    }

    @Test
    public void testResolveAttack_samePowerBothHaveDefeatedEffect() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        currentPlayer.addCardToBoard(attackCard, false);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        opponent.addCardToBoard(defendingCard, false);

        game.resolveAttack(attackCard, defendingCard, opponent);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));

        assertEquals(0, game.getEffectQueue().size());
        assertNull(game.getChoiceList());

        assertNotNull(game.getChoice());
        assertEquals(2, game.getChoice().size());
        for (Choice choice : game.getChoice()) {
            assertEquals(EffectTiming.DEFEATED, game.getChoice().getEffectTiming());
            assertEquals(ChoiceLocation.DISCARD, choice.getLocation());
            assertTrue(choice.getCard().equals(attackCard) || choice.getCard().equals(defendingCard));
        }
    }

    @Test
    public void testResolveSimultaneousChoice() {
        CardInstance attackCard = currentPlayer.getHand().removeFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        currentPlayer.getDiscardPile().add(attackCard);

        CardInstance defendingCard = opponent.getHand().removeFirst();
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        opponent.getDiscardPile().add(defendingCard);

        SimultaneousChoice choice = new SimultaneousChoice(currentPlayer, EffectTiming.DEFEATED);
        choice.add(new Choice(attackCard, ChoiceLocation.DISCARD));
        choice.add(new Choice(defendingCard, ChoiceLocation.DISCARD));
        game.setChoice(choice);

        game.resolveSimultaneousChoice(choice);

        assertNull(game.getChoice());
        assertEquals(2, game.getEffectQueue().size());

        for (EffectToApply effect : game.getEffectQueue()) {
            assertTrue(effect.getCard().equals(attackCard) || effect.getCard().equals(defendingCard));
        }
    }
}
