package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GainEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private GainEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new GainEffect();
    }

    @Test
    public void testBasic() {
        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(5, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_noEffect() {
        effect.setEqual(true);
        effect.apply(game, randomCard);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_moreLifePoints() {
        opponentPlayer.getTeam().setLifePoints(5);

        effect.setEqual(true);
        effect.apply(game, randomCard);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_lessLifePoints() {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setEqual(true);
        effect.apply(game, randomCard);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }
}
