package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoAttackEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
    }

    @Test
    public void testWithLowestParameter() {
        NoAttackEffect effect = new NoAttackEffect();
        effect.setLowest(true);

        // Nothing happens if opponent has no cards on board
        effect.apply(game, randomCard);

        // Add one creature to the opponent board and checks that it cannot attack
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard, false);
        effect.apply(game, randomCard);
        assertFalse(otherCard.isCanAttack());

        // Add an other creature (with same power) to the opponent board and checks that it cannot attack either
        otherCard.reset();
        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2, false);
        effect.apply(game, randomCard);
        assertFalse(otherCard.isCanAttack());
        assertFalse(otherCard2.isCanAttack());

        // Add an other creature (with an higher power) to the opponent board and checks that it can still attack
        otherCard.reset();
        otherCard2.reset();
        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        otherCard3.setPower(otherCard.getPower() + 1);
        opponentPlayer.addCardToBoard(otherCard3, false);
        effect.apply(game, randomCard);
        assertFalse(otherCard.isCanAttack());
        assertFalse(otherCard2.isCanAttack());
        assertTrue(otherCard3.isCanAttack());

        // Add an other creature (with the lowest power) to the opponent board and checks that it can still attack
        otherCard.reset();
        otherCard2.reset();
        otherCard3.reset();
        CardInstance otherCard4 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        otherCard3.setPower(otherCard.getPower() + 1);
        otherCard4.setPower(otherCard.getPower() - 1);
        opponentPlayer.addCardToBoard(otherCard4, false);
        effect.apply(game, randomCard);
        assertTrue(otherCard.isCanAttack());
        assertTrue(otherCard2.isCanAttack());
        assertTrue(otherCard3.isCanAttack());
        assertFalse(otherCard4.isCanAttack());
    }
}
