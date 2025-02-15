package org.metacorp.mindbug.card.effect.steal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class StealEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.setStillTough(false);
        currentPlayer.addCardToBoard(randomCard);

        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    //TODO Test choice resolution

    @Test
    public void testMin_noTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMin(6);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMin_badTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMin(6);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(5);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMin_singleTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMin(6);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(6);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(5, currentPlayer.getHand().size());
        assertTrue(currentPlayer.getHand().contains(otherCard2));
        assertEquals(1, opponentPlayer.getBoard().size());
        assertFalse(opponentPlayer.getBoard().contains(otherCard2));
        assertNull(game.getChoice());
    }

    @Test
    public void testMin_multipleTargets() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMin(6);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(9);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(6);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNotNull(game.getChoice());
    }

    @Test
    public void testMax_noTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMax(6);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMax_badTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMax(2);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(5);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMax_singleTarget() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMax(2);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(5, currentPlayer.getHand().size());
        assertTrue(currentPlayer.getHand().contains(otherCard2));
        assertEquals(1, opponentPlayer.getBoard().size());
        assertFalse(opponentPlayer.getBoard().contains(otherCard2));
        assertNull(game.getChoice());
    }

    @Test
    public void testMax_multipleTargets() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMax(2);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(2);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNotNull(game.getChoice());
    }

    @Test
    public void testRandom_nominal() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setRandom(true);
        stealEffect.setValue(2);
        stealEffect.setSource(StealSource.HAND);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(2);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(6, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getHand().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMayPlay_nominal() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMax(2);
        stealEffect.setMayPlay(true);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(1, opponentPlayer.getBoard().size());
        assertFalse(opponentPlayer.getBoard().contains(otherCard2));
        assertNotNull(game.getChoice());
    }

    @Test
    public void testMustPlay_nominal() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMin(4);
        stealEffect.setMustPlay(true);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(4);
        opponentPlayer.addCardToBoard(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(2, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertTrue(currentPlayer.getBoard().contains(otherCard2));
        assertEquals(1, opponentPlayer.getBoard().size());
        assertFalse(opponentPlayer.getBoard().contains(otherCard2));
        assertNull(game.getChoice());
    }

    @Test
    public void testSourceDiscard_nominal() {
        StealEffect stealEffect = new StealEffect();
        stealEffect.setMustPlay(true);
        stealEffect.setValue(1);
        stealEffect.setSource(StealSource.DISCARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(4);
        opponentPlayer.addCardToBoard(otherCard2);
        opponentPlayer.addCardToDiscardPile(otherCard2);

        stealEffect.apply(game, randomCard);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(2, currentPlayer.getBoard().size());
        assertTrue(currentPlayer.getBoard().contains(otherCard2));
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(1, opponentPlayer.getBoard().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());
        assertNull(game.getChoice());
    }
}
