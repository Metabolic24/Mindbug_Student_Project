package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerUpEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testWithLifePointsCondition() {
        PowerUpEffect powerUpEffect = new PowerUpEffect();
        powerUpEffect.setLifePoints(1);
        powerUpEffect.setValue(8);

        // Should have no effect as life points are currently 3
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());

        // Change life points to the expected value and check that card power has been changed
        currentPlayer.getTeam().setLifePoints(powerUpEffect.getLifePoints());
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower() + powerUpEffect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition() {
        PowerUpEffect powerUpEffect = new PowerUpEffect();
        powerUpEffect.setAlone(true);
        powerUpEffect.setValue(4);

        // Should have no effect as there are no card on player board
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());

        // Add the card to the board and check that card power has been changed
        currentPlayer.addCardToBoard(randomCard, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower() + powerUpEffect.getValue(), randomCard.getPower());

        // Add an other card to the board and check that card power is back to its initial value
        randomCard.reset();
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAlliesCondition() {
        PowerUpEffect powerUpEffect = new PowerUpEffect();
        powerUpEffect.setAllies(true);
        powerUpEffect.setSelf(false);
        powerUpEffect.setValue(3);

        // Should have no effect as there are only one card on player board
        currentPlayer.addCardToBoard(randomCard, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());

        // Add an other card on the board and check that its power is up by the effect
        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
        assertEquals(otherCard.getCard().getPower() + powerUpEffect.getValue(), otherCard.getPower());

        // Add 3 other cards on the board and check that their power is up by the effect
        otherCard.reset();
        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard2, false);
        CardInstance otherCard3 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard3, false);
        CardInstance otherCard4 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard4, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
        assertEquals(otherCard.getCard().getPower() + powerUpEffect.getValue(), otherCard.getPower());
        assertEquals(otherCard2.getCard().getPower() + powerUpEffect.getValue(), otherCard2.getPower());
        assertEquals(otherCard3.getCard().getPower() + powerUpEffect.getValue(), otherCard3.getPower());
        assertEquals(otherCard4.getCard().getPower() + powerUpEffect.getValue(), otherCard4.getPower());
    }

    @Test
    public void testWithSelfTurnCondition() {
        PowerUpEffect powerUpEffect = new PowerUpEffect();
        powerUpEffect.setSelfTurn(true);
        powerUpEffect.setValue(5);

        // Check that power up effect is correctly applied to the random card
        currentPlayer.addCardToBoard(randomCard, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower() + powerUpEffect.getValue(), randomCard.getPower());

        // Update current player value then check that the random card is no more powered up
        randomCard.reset();
        game.setCurrentPlayer(opponentPlayer);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithByEnemyCondition() {
        PowerUpEffect powerUpEffect = new PowerUpEffect();
        powerUpEffect.setByEnemy(true);
        powerUpEffect.setValue(2);

        // Should have no effect as there are no enemy creature for the moment
        currentPlayer.addCardToBoard(randomCard, false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());

        // Add 3 enemy creatures on board and check that current player card is correctly powered up
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        powerUpEffect.apply(game, randomCard);
        assertEquals(randomCard.getCard().getPower() + 3 * powerUpEffect.getValue(), randomCard.getPower());
    }
}
