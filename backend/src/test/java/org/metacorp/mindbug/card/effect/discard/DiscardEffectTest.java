package org.metacorp.mindbug.card.effect.discard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.player.Player;

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

    //TODO Test choice resolution

    @Test
    public void testBasic_opponentHandIs2() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs3() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs5() {
        effect.apply(game, randomCard);

        assertEquals(5, opponentPlayer.getHand().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertEquals(3, targetChoice.getTargetsCount());
        assertEquals(effect, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(opponentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(5, targetChoice.getAvailableTargets().size());

        for (CardInstance card : opponentPlayer.getHand()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(card::equals).count());
        }
    }
}
