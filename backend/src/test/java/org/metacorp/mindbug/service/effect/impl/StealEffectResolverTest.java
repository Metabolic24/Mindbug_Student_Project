package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.StealEffect;
import org.metacorp.mindbug.model.effect.steal.StealSource;
import org.metacorp.mindbug.model.effect.steal.StealTargetSelection;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class StealEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private StealEffect effect;
    private StealEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.setStillTough(false);
        currentPlayer.addCardToBoard(randomCard);

        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new StealEffect();
        effectResolver = new StealEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    //TODO Test choice resolution

    @Test
    public void testMin_noTarget() {
        effect.setMin(6);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMin_badTarget() {
        effect.setMin(6);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(5);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMin_singleTarget() {
        effect.setMin(6);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(6);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

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
        effect.setMin(6);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(9);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(6);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNotNull(game.getChoice());
    }

    @Test
    public void testMax_noTarget() {
        effect.setMax(6);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMax_badTarget() {
        effect.setMax(2);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(5);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMax_singleTarget() {
        effect.setMax(2);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

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
        effect.setMax(2);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(2);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertNotNull(game.getChoice());
    }

    @Test
    public void testRandom_nominal() {
        effect.setSelection(StealTargetSelection.RANDOM);
        effect.setValue(2);
        effect.setSource(StealSource.HAND);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(2);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(6, currentPlayer.getHand().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getHand().size());
        assertNull(game.getChoice());
    }

    @Test
    public void testMayPlay_nominal() {
        effect.setMax(2);
        effect.setMayPlay(true);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(1);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(1, opponentPlayer.getBoard().size());
        assertFalse(opponentPlayer.getBoard().contains(otherCard2));
        assertNotNull(game.getChoice());
    }

    @Test
    public void testMustPlay_nominal() {
        effect.setMin(4);
        effect.setMustPlay(true);
        effect.setValue(1);
        effect.setSource(StealSource.BOARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(3);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(4);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

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
        effect.setMustPlay(true);
        effect.setValue(1);
        effect.setSource(StealSource.DISCARD);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.getEffects(EffectTiming.PLAY).clear();

        opponentPlayer.addCardToBoard(otherCard);
        opponentPlayer.addCardToDiscardPile(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertEquals(2, currentPlayer.getBoard().size());
        assertTrue(currentPlayer.getBoard().contains(otherCard));
        assertEquals(4, currentPlayer.getHand().size());
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(otherCard2, opponentPlayer.getBoard().getFirst());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());
        assertNull(game.getChoice());
    }
}
