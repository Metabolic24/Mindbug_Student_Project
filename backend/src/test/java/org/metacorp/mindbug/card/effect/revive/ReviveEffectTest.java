package org.metacorp.mindbug.card.effect.revive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.bool.BooleanChoice;
import org.metacorp.mindbug.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class ReviveEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private ReviveEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
        randomCard = opponentPlayer.getHand().removeFirst();
        opponentPlayer.getDiscardPile().add(randomCard);

        effect = new ReviveEffect();
    }

    //TODO Tester la résolution des choix

    @Test
    public void testBasic() {
        effect.setLoseLife(true);
        effect.apply(game, randomCard);

        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));

        assertNotNull(game.getCurrentChoice());
        assertEquals(ChoiceType.BOOLEAN, game.getCurrentChoice().getType());

        BooleanChoice booleanChoice = (BooleanChoice) game.getCurrentChoice();

        assertEquals(effect, booleanChoice.getEffect());
        assertEquals(randomCard, booleanChoice.getCard());
        assertEquals(opponentPlayer, booleanChoice.getPlayerToChoose());
    }
}
