package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrawEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
    }

    @Test
    public void testBasic() {
        DrawEffect effect = new DrawEffect();
        effect.setValue(2);

        // Check that current player draw 2 cards from his pile
        effect.apply(game, randomCard);
        assertEquals(7, currentPlayer.getHand().size());
        assertEquals(3, currentPlayer.getDrawPile().size());

        // Check that current player draw all the remaining cards from his pile
        effect.setValue(4);
        effect.apply(game, randomCard);
        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testWithSelfDiscardParameter() {
        DrawEffect effect = new DrawEffect();
        effect.setSelfDiscard(true);

        // Nothing should happen as discard pile is empty
        effect.apply(game, randomCard);
        assertEquals(5, currentPlayer.getHand().size());

        // Add 3 cards from draw pile to discard one then check that all these cards are in hand after effect has been applied
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());
        currentPlayer.getDiscardPile().add(currentPlayer.getDrawPile().removeFirst());

        effect.apply(game, randomCard);
        assertEquals(8, currentPlayer.getHand().size());
    }
}
