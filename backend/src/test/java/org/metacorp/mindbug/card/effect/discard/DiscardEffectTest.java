package org.metacorp.mindbug.card.effect.discard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.discard.DiscardEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;
    private DiscardEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new DiscardEffect();
        effect.setValue(3);
    }

    @Test
    public void testBasic_opponentHandIs2() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs3() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs5() {
        effect.apply(game, randomCard);

        assertEquals(5, opponentPlayer.getHand().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());

        assertNull(game.getChoice());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(3, choiceList.getChoicesCount());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(opponentPlayer, choiceList.getPlayerToChoose());
        assertEquals(5, choiceList.getChoices().size());

        for (CardInstance card : opponentPlayer.getHand()) {
            assertEquals(1, choiceList.getChoices().stream()
                    .filter(choice -> choice.getCard().equals(card) && choice.getLocation() == ChoiceLocation.HAND)
                    .count());
        }
    }
}
