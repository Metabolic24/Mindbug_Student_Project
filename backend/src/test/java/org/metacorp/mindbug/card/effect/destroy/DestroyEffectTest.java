package org.metacorp.mindbug.card.effect.destroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.destroy.DestroyEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.gainLp.GainEffect;
import org.metacorp.mindbug.card.effect.inflict.InflictEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;
import org.metacorp.mindbug.choice.SimultaneousChoice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestroyEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        randomCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(randomCard, false);

        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testLowestAndSelfAllowed_singleCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_noCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_singleOpponentCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Opponent card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_opponentCardLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower() - 1);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_samePower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower());

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower()+1);

        // Current player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_multipleCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance currentPlayerCard1 = currentPlayer.getHand().getFirst();
        currentPlayerCard1.setPower(randomCard.getPower() + 4);
        currentPlayer.addCardToBoard(currentPlayerCard1, false);

        CardInstance currentPlayerCard2 = currentPlayer.getHand().getFirst();
        currentPlayerCard2.setPower(randomCard.getPower() - 1);
        currentPlayer.addCardToBoard(currentPlayerCard2, false);

        CardInstance currentPlayerCard3 = currentPlayer.getHand().getFirst();
        currentPlayerCard3.setPower(randomCard.getPower() - 2);
        currentPlayer.addCardToBoard(currentPlayerCard3, false);

        CardInstance opponentPlayerCard1 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard1.setPower(randomCard.getPower() - 2);
        opponentPlayer.addCardToBoard(opponentPlayerCard1, false);

        CardInstance opponentPlayerCard2 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard2.setPower(randomCard.getPower() + 1);
        opponentPlayer.addCardToBoard(opponentPlayerCard2, false);

        CardInstance opponentPlayerCard3= opponentPlayer.getHand().getFirst();
        opponentPlayerCard3.setPower(randomCard.getPower() - 2);
        opponentPlayer.addCardToBoard(opponentPlayerCard3, false);

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_noCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleOpponentCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Opponent card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_opponentCardLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower() - 1);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_samePower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower());

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(randomCard.getPower()+1);

        // Current player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_multipleCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance currentPlayerCard1 = currentPlayer.getHand().getFirst();
        currentPlayerCard1.setPower(randomCard.getPower() + 4);
        currentPlayer.addCardToBoard(currentPlayerCard1, false);

        CardInstance currentPlayerCard2 = currentPlayer.getHand().getFirst();
        currentPlayerCard2.setPower(randomCard.getPower() - 1);
        currentPlayer.addCardToBoard(currentPlayerCard2, false);

        CardInstance currentPlayerCard3 = currentPlayer.getHand().getFirst();
        currentPlayerCard3.setPower(randomCard.getPower() - 2);
        currentPlayer.addCardToBoard(currentPlayerCard3, false);

        CardInstance opponentPlayerCard1 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard1.setPower(randomCard.getPower() - 2);
        opponentPlayer.addCardToBoard(opponentPlayerCard1, false);

        CardInstance opponentPlayerCard2 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard2.setPower(randomCard.getPower() + 1);
        opponentPlayer.addCardToBoard(opponentPlayerCard2, false);

        CardInstance opponentPlayerCard3= opponentPlayer.getHand().getFirst();
        opponentPlayerCard3.setPower(randomCard.getPower() - 2);
        opponentPlayer.addCardToBoard(opponentPlayerCard3, false);

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_moreAllies() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        // Nothing should happen as current player has more allies than the opponent
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_sameAlliesCount() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Nothing should happen as current player has as much allies as the opponent
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Effect should trigger as current player has less allies than the opponent
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(currentPlayer, choiceList.getPlayerToChoose());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(1, choiceList.getChoicesCount());
        assertEquals(2, choiceList.getChoices().size());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(1, choiceList.getChoices().stream()
                    .filter(choice -> choice.getCard().equals(card) && choice.getLocation() == ChoiceLocation.BOARD)
                    .count());
        }
    }

    @Test
    public void testLessAllies_lessAlliesAndValueSame() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(2);
        effect.setLessAllies(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueHigher() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(4);
        effect.setLessAllies(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(2, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_lowerPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        card.setPower(2);

        // Card should not be destroyed
        effect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        card.setPower(8);

        // Card should not be destroyed
        effect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_samePowerThanMin() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        card.setPower(3);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_powerInInterval() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        card.setPower(4);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_samePowerThanMax() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        card.setPower(5);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_selfAllowed() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);
        effect.setSelfAllowed(true);

        randomCard.setPower(5);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getBoard().size());
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
        assertTrue(currentPlayer.getDiscardPile().contains(randomCard));
    }

    @Test
    public void testMinAndMax_choice() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        opponentPlayer.addCardToBoard(card, false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        opponentPlayer.addCardToBoard(otherCard, false);

        // Cards should not be destroyed but a choice should be created
        effect.apply(game, randomCard);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(currentPlayer, choiceList.getPlayerToChoose());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(1, choiceList.getChoicesCount());
        assertEquals(2, choiceList.getChoices().size());

        for (CardInstance opponentCard : opponentPlayer.getBoard()) {
            assertEquals(1, choiceList.getChoices().stream()
                    .filter(choice -> choice.getCard().equals(opponentCard) && choice.getLocation() == ChoiceLocation.BOARD)
                    .count());
        }
    }

    @Test
    public void testMinAndMax_choiceAndSelfAllowed() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        opponentPlayer.addCardToBoard(card, false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        opponentPlayer.addCardToBoard(otherCard, false);

        randomCard.setPower(4);
        effect.setSelfAllowed(true);

        // Card should be destroyed but a choice should be created
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        ChoiceList choiceList = game.getChoiceList();
        assertNotNull(choiceList);
        assertEquals(currentPlayer, choiceList.getPlayerToChoose());
        assertEquals(effect, choiceList.getSourceEffect());
        assertEquals(randomCard, choiceList.getSourceCard());
        assertEquals(1, choiceList.getChoicesCount());
        assertEquals(3, choiceList.getChoices().size());

        for (CardInstance opponentCard : opponentPlayer.getBoard()) {
            assertEquals(1, choiceList.getChoices().stream()
                    .filter(choice -> choice.getCard().equals(opponentCard) && choice.getLocation() == ChoiceLocation.BOARD)
                    .count());
        }

        assertEquals(1, choiceList.getChoices().stream()
                .filter(choice -> choice.getCard().equals(randomCard) && choice.getLocation() == ChoiceLocation.BOARD)
                .count());
    }

    @Test
    public void testDestroyCards_basic() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Check that card is destroyed and that its DEFEATED effects (if any) are added to the effect queue
        effect.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        if (!randomCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
            assertEquals(randomCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        }
    }

    @Test
    public void testDestroyCards_multiple() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(2);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);

        card.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new InflictEffect()));

        // Check that cards are destroyed and that a simultaneous choice is created for the cards effects
        effect.apply(game, randomCard);
        assertEquals(0,  opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
        assertTrue(game.getEffectQueue().isEmpty());

        SimultaneousChoice choice = game.getChoice();
        assertNotNull(choice);
        assertEquals(EffectTiming.DEFEATED, choice.getEffectTiming());
        assertEquals(currentPlayer, choice.getPlayerToChoose());
        assertEquals(2, choice.size());

        for (CardInstance currentCard : opponentPlayer.getDiscardPile()) {
            assertEquals(1, choice.stream()
                    .filter(currentChoice -> currentChoice.getCard().equals(currentCard) && currentChoice.getLocation() == ChoiceLocation.DISCARD)
                    .count());
        }
    }
}
