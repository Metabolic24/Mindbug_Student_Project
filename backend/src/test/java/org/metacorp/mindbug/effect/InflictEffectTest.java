package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InflictEffectTest {

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
        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(2);

        inflictEffect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithSelfParameter() {
        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(4);
        inflictEffect.setSelf(true);

        inflictEffect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getTeam().getLifePoints());
        assertEquals(3, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter() {
        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setAllButOne(true);

        // It should have no effect as both players have 3 life points
        inflictEffect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getTeam().getLifePoints());

        // Apply it again as it should have no effect
        inflictEffect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }
}

