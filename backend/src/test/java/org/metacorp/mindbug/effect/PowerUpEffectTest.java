package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerUpEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private PowerUpEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new PowerUpEffect();
    }

    @Test
    public void testWithLifePointsCondition_moreLifePoints() {
        effect.setLifePoints(1);
        effect.setValue(8);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithLifePointsCondition_sameLifePoints() {
        effect.setLifePoints(3);
        effect.setValue(8);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithLifePointsCondition_lessLifePoints() {
        effect.setLifePoints(4);
        effect.setValue(8);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_noEffect() {
        effect.setAlone(true);
        effect.setValue(4);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_singleCard() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setAlone(true);
        effect.setValue(4);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_multipleCards() {
        currentPlayer.addCardToBoard(randomCard);
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        effect.setAlone(true);
        effect.setValue(4);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAlliesCondition_noEffect() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setAllies(true);
        effect.setSelf(false);
        effect.setValue(3);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAlliesCondition_twoCards() {
        currentPlayer.addCardToBoard(randomCard);

        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard);

        effect.setAllies(true);
        effect.setSelf(false);
        effect.setValue(3);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
        assertEquals(otherCard.getCard().getPower() + effect.getValue(), otherCard.getPower());
    }

    @Test
    public void testWithAlliesCondition_fiveCards() {
        currentPlayer.addCardToBoard(randomCard);

        CardInstance otherCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard3);

        CardInstance otherCard4 = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(otherCard4);

        effect.setAllies(true);
        effect.setSelf(false);
        effect.setValue(3);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
        assertEquals(otherCard.getCard().getPower() + effect.getValue(), otherCard.getPower());
        assertEquals(otherCard2.getCard().getPower() + effect.getValue(), otherCard2.getPower());
        assertEquals(otherCard3.getCard().getPower() + effect.getValue(), otherCard3.getPower());
        assertEquals(otherCard4.getCard().getPower() + effect.getValue(), otherCard4.getPower());
    }

    @Test
    public void testWithSelfTurnCondition_selfTurn() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setSelfTurn(true);
        effect.setValue(5);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithSelfTurnCondition_opponentTurn() {
        currentPlayer.addCardToBoard(randomCard);

        game.setCurrentPlayer(opponentPlayer);

        effect.setSelfTurn(true);
        effect.setValue(5);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithByEnemyCondition_noEffect() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setByEnemy(true);
        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithByEnemyCondition_threeEnemies() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        currentPlayer.addCardToBoard(randomCard);

        effect.setByEnemy(true);
        effect.setValue(2);
        effect.apply(game, randomCard);

        assertEquals(randomCard.getCard().getPower() + (3 * effect.getValue()), randomCard.getPower());
    }
}
