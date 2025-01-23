package org.metacorp.mindbug.card.effect.noBlock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.noBlock.NoBlockEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoBlockEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private NoBlockEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new NoBlockEffect();
    }

    @Test
    public void testBasic_lessThanBoardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard, false);
        CardInstance secondCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(secondCard, false);

        effect.setValue(1);
        effect.apply(game, randomCard);

        assertTrue(firstCard.isCanBlock());
        assertTrue(secondCard.isCanBlock());

        assertNull(game.getChoice());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(1, choiceList.getChoicesCount());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(game.getCurrentPlayer(), choiceList.getPlayerToChoose());
        assertEquals(2, choiceList.getChoices().size());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(1, choiceList.getChoices().stream()
                    .filter(choice -> choice.getCard().equals(card) && choice.getLocation() == ChoiceLocation.BOARD)
                    .count());
        }
    }

    @Test
    public void testBasic_boardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard, false);
        CardInstance secondCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(secondCard, false);

        effect.setValue(2);
        effect.apply(game, randomCard);

        assertFalse(firstCard.isCanBlock());
        assertFalse(secondCard.isCanBlock());

        assertNull(game.getChoice());
        assertNull(game.getChoiceList());
    }

    @Test
    public void testBasic_moreThanBoardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard, false);

        effect.setValue(2);
        effect.apply(game, randomCard);

        assertFalse(firstCard.isCanBlock());
        assertNull(game.getChoice());
        assertNull(game.getChoiceList());
    }

    @Test
    public void testWithMaxCondition_noEffect() {
        effect.setMax(5);
        effect.apply(game, randomCard);
    }

    @Test
    public void testWithMaxCondition_singleMatchingCardSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        effect.setMax(otherCard.getPower());
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
    }

    @Test
    public void testWithMaxCondition_twoMatchingCardSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2, false);

        effect.setMax(otherCard.getPower());
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
        assertFalse(otherCard2.isCanBlock());
    }

    @Test
    public void testWithMaxCondition_twoMatchingCardDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 1);
        opponentPlayer.addCardToBoard(otherCard2, false);

        effect.setMax(otherCard.getPower());
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
        assertFalse(otherCard2.isCanBlock());
    }

    @Test
    public void testWithMaxCondition_twoCardsOneMatching() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() + 1);
        opponentPlayer.addCardToBoard(otherCard2, false);

        effect.setMax(otherCard.getPower());
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
        assertTrue(otherCard2.isCanBlock());
    }

    @Test
    public void testWithHighestParameter_singleCard() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        effect.setHighest(true);
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
    }

    @Test
    public void testWithHighestParameter_twoCardsSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2, false);

        effect.setHighest(true);
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
        assertFalse(otherCard2.isCanBlock());
    }

    @Test
    public void testWithHighestParameter_twoCardsDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 2);
        opponentPlayer.addCardToBoard(otherCard2, false);

        effect.setHighest(true);
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanBlock());
        assertTrue(otherCard2.isCanBlock());
    }

    @Test
    public void testWithHighestParameter_threeCardsDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 2);
        opponentPlayer.addCardToBoard(otherCard2, false);

        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard3.setPower(otherCard.getPower() + 4);
        opponentPlayer.addCardToBoard(otherCard3, false);

        effect.setHighest(true);
        effect.apply(game, randomCard);

        assertTrue(otherCard.isCanBlock());
        assertTrue(otherCard2.isCanBlock());
        assertFalse(otherCard3.isCanBlock());
    }
}
