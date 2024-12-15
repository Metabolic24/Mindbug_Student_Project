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

    private InflictEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new InflictEffect();
    }

    @Test
    public void testBasic() {
        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithSelfParameter() {
        effect.setValue(4);
        effect.setSelf(true);
        effect.apply(game, randomCard);

        assertEquals(0, currentPlayer.getTeam().getLifePoints());
        assertEquals(3, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_nominal() {
        effect.setAllButOne(true);
        effect.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_noEffect() {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setAllButOne(true);
        effect.apply(game, randomCard);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }
}
