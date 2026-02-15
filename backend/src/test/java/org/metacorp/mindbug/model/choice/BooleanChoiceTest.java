package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.MindbugGameTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BooleanChoiceTest extends MindbugGameTest {

    private Game game;
    private Player currentPlayer;
    private CardInstance currentCard;
    private ResolvableEffect<Boolean> effect;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        currentCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(currentCard);
        effect = (_, value) -> {
            if (value) {
                currentPlayer.getTeam().setLifePoints(currentPlayer.getTeam().getLifePoints() + 1);
            } else {
                currentPlayer.getTeam().setLifePoints(currentPlayer.getTeam().getLifePoints() - 1);
            }
        };
    }

    @Test
    public void testResolve_true() throws GameStateException, WebSocketException {
        BooleanChoice choice = new BooleanChoice(currentPlayer, currentCard, effect);
        game.setChoice(choice);

        choice.resolve(true, game);

        assertNull(game.getChoice());
        assertEquals(4, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testResolve_false() throws GameStateException, WebSocketException {
        BooleanChoice choice = new BooleanChoice(currentPlayer, currentCard, effect);
        game.setChoice(choice);

        choice.resolve(false, game);

        assertNull(game.getChoice());
        assertEquals(2, currentPlayer.getTeam().getLifePoints());
    }
}
