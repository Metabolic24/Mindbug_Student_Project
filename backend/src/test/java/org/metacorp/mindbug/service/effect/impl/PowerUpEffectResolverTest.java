package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerUpEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private PowerUpEffect effect;
    private PowerUpEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new PowerUpEffect();
        effectResolver = new PowerUpEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testWithLifePointsCondition_moreLifePoints() {
        effect.setLifePoints(1);
        effect.setValue(8);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithLifePointsCondition_sameLifePoints() {
        effect.setLifePoints(3);
        effect.setValue(8);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithLifePointsCondition_lessLifePoints() {
        effect.setLifePoints(4);
        effect.setValue(8);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_noEffect() {
        effect.setAlone(true);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_singleCard() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setAlone(true);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithAloneCondition_multipleCards() {
        currentPlayer.addCardToBoard(randomCard);
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        effect.setAlone(true);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAlliesCondition_noEffect() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setAllies(true);
        effect.setSelf(false);
        effect.setValue(3);
        effectResolver.apply(game, randomCard, timing);

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
        effectResolver.apply(game, randomCard, timing);

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
        effectResolver.apply(game, randomCard, timing);

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
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + effect.getValue(), randomCard.getPower());
    }

    @Test
    public void testWithSelfTurnCondition_opponentTurn() {
        currentPlayer.addCardToBoard(randomCard);

        game.setCurrentPlayer(opponentPlayer);

        effect.setSelfTurn(true);
        effect.setValue(5);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithForEachAllyCondition_noEffect() {
        currentPlayer.addCardToBoard(randomCard);

        effect.setForEachAlly(true);
        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithForEachAllyCondition_threeEnemies() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        currentPlayer.addCardToBoard(randomCard);

        effect.setForEachAlly(true);
        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + (3 * effect.getValue()), randomCard.getPower());
    }

    @Test
    public void testWithEnemiesCount_nominal() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setEnemiesCount(3);
        effect.setValue(5);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + 5, randomCard.getPower());
    }

    @Test
    public void testWithEnemiesCount_noEffect() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setEnemiesCount(3);
        effect.setValue(5);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithAlliesCount_nominal() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setAlliesCount(2);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + 4, randomCard.getPower());
    }

    @Test
    public void testWithAlliesCount_noEffect() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setAlliesCount(3);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }

    @Test
    public void testWithNoMindbug_nominal() {
        currentPlayer.setMindBugs(0);

        effect.setNoMindbug(true);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower() + 4, randomCard.getPower());
    }

    @Test
    public void testWithNoMindbug_noEffect() {
        currentPlayer.setMindBugs(1);

        effect.setNoMindbug(true);
        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(randomCard.getCard().getPower(), randomCard.getPower());
    }
}
