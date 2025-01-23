package org.metacorp.mindbug.card.effect.revive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;
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

    @Test
    public void testBasic() {
        effect.setLoseLife(true);
        effect.apply(game, randomCard);

        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));

        assertNull(game.getChoice());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(0, choiceList.getChoicesCount());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(randomCard.getOwner(), choiceList.getPlayerToChoose());
        assertEquals(1, choiceList.getChoices().size());
        assertEquals(1, choiceList.getChoices().stream()
                .filter(choice -> choice.getCard().equals(randomCard) && choice.getLocation() == ChoiceLocation.DISCARD)
                .count());
    }
}
