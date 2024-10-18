package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GainEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testBasic() {
        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(2);

        gainEffect.apply(game, randomCard);
        assertEquals(5, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter() {
        GainEffect gainEffect = new GainEffect();
        gainEffect.setEqual(true);

        // It should have no effect as both players have 3 life points
        gainEffect.apply(game, randomCard);
        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());

        // Check that it works if opponent has more life points
        opponentPlayer.getTeam().setLifePoints(5);
        gainEffect.apply(game, randomCard);
        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());

        // Check that it works if opponent has more life points
        opponentPlayer.getTeam().setLifePoints(1);
        gainEffect.apply(game, randomCard);
        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }
}
