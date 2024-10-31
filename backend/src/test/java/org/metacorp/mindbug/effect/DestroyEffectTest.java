package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.EffectTiming;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;
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
    public void testLowestAndSelfAllowed() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        currentPlayer.getDiscardPile().clear();

        // Nothing should happen as there are no cards on board
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Opponent card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        opponentPlayer.getDiscardPile().clear();
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower() - 1);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        opponentPlayer.getDiscardPile().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower());

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        currentPlayer.getDiscardPile().clear();
        opponentPlayer.getDiscardPile().clear();
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower()+1);

        // Current player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        currentPlayer.getDiscardPile().clear();

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower() - 1);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        opponentPlayer.getDiscardPile().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower());

        // Still opponent card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        opponentPlayer.getDiscardPile().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.getBoard().getFirst().setPower(currentPlayer.getBoard().getFirst().getPower()+1);

        // Still opponent card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        opponentPlayer.getDiscardPile().clear();
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(opponentCard, false);
        CardInstance otherOpponentCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherOpponentCard, false);
        otherOpponentCard.setPower(opponentCard.getPower() - 1);

        // One opponent card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertEquals(otherOpponentCard, opponentPlayer.getDiscardPile().getFirst());

        opponentPlayer.getDiscardPile().clear();
        opponentPlayer.getBoard().add(otherOpponentCard);
        otherOpponentCard.setPower(opponentCard.getPower() + 1);

        // One opponent card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertEquals(opponentCard, opponentPlayer.getDiscardPile().getFirst());

        opponentPlayer.getDiscardPile().clear();
        opponentPlayer.getBoard().add(opponentCard);
        otherOpponentCard.setPower(opponentCard.getPower());

        // One opponent card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(opponentCard));
        assertTrue(opponentPlayer.getDiscardPile().contains(otherOpponentCard));
    }

    @Test
    public void testWithLessAllies() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        // Nothing should happen as current player has more allies than the opponent
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Nothing should happen as current player has as much allies than the opponent
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getBoard().size());

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

        // Effect should trigger as current player has less allies than the opponent
        game.setChoiceList(null);
        effect.setValue(2);

        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testWithMinAndMaxWithoutChoice() {
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

        card.setPower(8);

        // Card should not be destroyed
        effect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        card.setPower(3);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));

        opponentPlayer.getBoard().add(opponentPlayer.getDiscardPile().removeFirst());

        card.setPower(4);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));

        opponentPlayer.getBoard().add(opponentPlayer.getDiscardPile().removeFirst());

        card.setPower(5);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));

        opponentPlayer.getDiscardPile().clear();
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
    public void testWithMinAndMaxWithChoice() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(card, false);
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);
        card.setPower(2);
        otherCard.setPower(2);

        // Cards should not be destroyed
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        card.setPower(8);
        otherCard.setPower(8);

        // Cards should not be destroyed
        effect.apply(game, randomCard);
        assertNull(game.getChoiceList());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        card.setPower(3);
        otherCard.setPower(3);

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

        game.setChoiceList(null);
        card.setPower(4);
        otherCard.setPower(4);

        // Card should be destroyed but a choice should be created
        effect.apply(game, randomCard);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        choiceList = game.getChoiceList();
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

        game.setChoiceList(null);
        card.setPower(5);
        otherCard.setPower(5);

        // Card should be destroyed but a choice should be created
        effect.apply(game, randomCard);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        choiceList = game.getChoiceList();
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

        game.setChoiceList(null);
        randomCard.setPower(4);
        effect.setSelfAllowed(true);

        // Card should be destroyed but a choice should be created
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        choiceList = game.getChoiceList();
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
    public void testDestroyCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);

        // Check that card is destroyed and that its DEFEATED effects (if any) are added to the effect queue
        effect.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(0,  opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        if (!randomCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
            assertEquals(randomCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        }

        opponentPlayer.getDiscardPile().clear();
        game.getEffectQueue().clear();
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
