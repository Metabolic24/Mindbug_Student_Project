package org.metacorp.mindbug.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AttackServiceTest {

    private Game game;
    private Player currentPlayer;
    private Player opponent;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponent = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testProcessAttackDeclaration_nominal() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);

        AttackService.processAttackDeclaration(attackCard, game);

        if (attackCard.getEffects(EffectTiming.ATTACK).isEmpty()) {
            assertTrue(game.getEffectQueue().isEmpty());
        } else {
            assertEquals(1, game.getEffectQueue().size());

            EffectsToApply effectsToApply = game.getEffectQueue().peek();
            assertNotNull(effectsToApply);
            assertEquals(attackCard.getEffects(EffectTiming.ATTACK).size(), effectsToApply.getEffects().size());
        }
    }

    @Test
    public void testProcessAttackDeclaration_attackRestricted() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);

        currentPlayer.disableTiming(EffectTiming.ATTACK);

        AttackService.processAttackDeclaration(attackCard, game);

        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void testProcessAttackResolution_noBlock() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        AttackService.processAttackResolution(attackCard, null, game);
        assertEquals(2, opponent.getTeam().getLifePoints());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testDeclareAttack_forcedTarget() throws GameStateException {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getEffects(EffectTiming.ATTACK).clear();
        attackCard.getEffects(EffectTiming.DEFEATED).clear();
        attackCard.getKeywords().remove(CardKeyword.POISONOUS);
        attackCard.getKeywords().remove(CardKeyword.SNEAKY);
        attackCard.setStillTough(false);
        currentPlayer.addCardToBoard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() + 1);
        opponent.addCardToBoard(defendingCard);

        game.setForcedTarget(defendingCard);
        AttackService.declareAttack(attackCard, game);

        assertTrue(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertNull(game.getAfterEffect());
        assertFalse(game.isForcedAttack());
        assertNull(game.getForcedTarget());
        assertNull(game.getAttackingCard());
    }


    @Test
    public void testProcessAttackResolution_lowerBlocker() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() - 1);
        defendingCard.setStillTough(false);
        defendingCard.getKeywords().remove(CardKeyword.POISONOUS);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

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
        attackCard.getKeywords().remove(CardKeyword.POISONOUS);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() + 1);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

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

        AttackService.processAttackResolution(attackCard, defendingCard, game);

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

        AttackService.processAttackResolution(attackCard, defendingCard, game);

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

        AttackService.processAttackResolution(attackCard, defendingCard, game);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));

        assertEquals(2, game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_attackReversed() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.getKeywords().add(CardKeyword.REVERSED);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.getKeywords().remove(CardKeyword.POISONOUS);
        defendingCard.setStillTough(false);
        defendingCard.setPower(attackCard.getPower() + 1);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertTrue(currentPlayer.getBoard().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_blockerReversed() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() + 1);
        defendingCard.getKeywords().remove(CardKeyword.POISONOUS);
        defendingCard.setStillTough(false);
        defendingCard.getKeywords().add(CardKeyword.REVERSED);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertTrue(currentPlayer.getBoard().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_bothReversed() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.setStillTough(false);
        attackCard.getKeywords().remove(CardKeyword.POISONOUS);
        attackCard.getKeywords().add(CardKeyword.REVERSED);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower() - 1);
        defendingCard.getKeywords().add(CardKeyword.REVERSED);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

        assertTrue(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertEquals(attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }

    @Test
    public void testProcessAttackResolution_reverseSamePower() {
        CardInstance attackCard = currentPlayer.getHand().getFirst();
        attackCard.setStillTough(false);
        attackCard.getKeywords().remove(CardKeyword.POISONOUS);
        currentPlayer.addCardToBoard(attackCard);
        game.setAttackingCard(attackCard);

        CardInstance defendingCard = opponent.getHand().getFirst();
        defendingCard.setPower(attackCard.getPower());
        defendingCard.setStillTough(false);
        defendingCard.getKeywords().remove(CardKeyword.POISONOUS);
        defendingCard.getKeywords().add(CardKeyword.REVERSED);
        opponent.addCardToBoard(defendingCard);

        AttackService.processAttackResolution(attackCard, defendingCard, game);

        assertFalse(opponent.getBoard().contains(defendingCard));
        assertFalse(currentPlayer.getBoard().contains(attackCard));
        assertTrue(currentPlayer.getDiscardPile().contains(attackCard));
        assertTrue(opponent.getDiscardPile().contains(defendingCard));
        assertEquals(defendingCard.getEffects(EffectTiming.DEFEATED).size() + attackCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        assertNotNull(game.getAfterEffect());
    }
}
