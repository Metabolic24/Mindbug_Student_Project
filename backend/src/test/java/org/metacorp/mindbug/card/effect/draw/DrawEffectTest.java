package org.metacorp.mindbug.card.effect.draw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrawEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;

    private DrawEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();

        effect = new DrawEffect();
    }

    @Test
    public void testBasic_draw2Over5() {
        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(7, currentPlayer.getHand().size());
        assertEquals(3, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testBasic_draw4Over3() {
        currentPlayer.drawX(2);
        assertEquals(7, currentPlayer.getHand().size());
        assertEquals(3, currentPlayer.getDrawPile().size());

        effect.setValue(4);
        effect.apply(game, randomCard);

        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testBasic_draw2Over0() {
        currentPlayer.drawX(5);
        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());

        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testWithSelfDiscard_emptyDiscard() {
        effect.setSelfDiscard(true);
        effect.apply(game, randomCard);

        assertEquals(5, currentPlayer.getHand().size());
    }

    @Test
    public void testWithSelfDiscard_discardSize3() {
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());

        assertEquals(3, currentPlayer.getDiscardPile().size());
        assertEquals(5, currentPlayer.getHand().size());

        effect.setSelfDiscard(true);
        effect.apply(game, randomCard);

        assertEquals(8, currentPlayer.getHand().size());
        assertTrue(currentPlayer.getDiscardPile().isEmpty());
    }
}
