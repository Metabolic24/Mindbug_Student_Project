package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscardEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
    }

    @Test
    public void testBasic() {
        DiscardEffect effect = new DiscardEffect();
        effect.setValue(3);

        // Check that effect applies if opponent hand size is 2
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(2, opponentPlayer.getDiscardPile().size());

        // Check that effect applies if opponent hand size is 3
        opponentPlayer.drawX(3);

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(5, opponentPlayer.getDiscardPile().size());
    }
}
