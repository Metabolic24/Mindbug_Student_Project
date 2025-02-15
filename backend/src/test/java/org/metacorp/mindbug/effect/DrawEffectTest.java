package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrawEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;

    private DrawEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        randomCard = currentPlayer.getHand().getFirst();

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
}
