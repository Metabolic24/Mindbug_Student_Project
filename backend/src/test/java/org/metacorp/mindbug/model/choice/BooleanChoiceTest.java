package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanChoiceTest {

    private Game game;
    private Player currentPlayer;
    private CardInstance currentCard;
    private ResolvableEffect<Boolean> effect;

    @BeforeEach
    public void initGame() {
        game = StartService.newGame("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        currentCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(currentCard);
        effect = (game, value) -> {
            if (value) {
                currentPlayer.getTeam().setLifePoints(currentPlayer.getTeam().getLifePoints() + 1);
            } else {
                currentPlayer.getTeam().setLifePoints(currentPlayer.getTeam().getLifePoints() - 1);
            }
        };
    }

    @Test
    public void testResolve_true() {
        BooleanChoice choice = new BooleanChoice(currentPlayer, currentCard, effect);
        game.setChoice(choice);

        choice.resolve(true, game);

        assertNull(game.getChoice());
        assertEquals(4, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testResolve_false() {
        BooleanChoice choice = new BooleanChoice(currentPlayer, currentCard, effect);
        game.setChoice(choice);

        choice.resolve(false, game);

        assertNull(game.getChoice());
        assertEquals(2, currentPlayer.getTeam().getLifePoints());
    }
}
