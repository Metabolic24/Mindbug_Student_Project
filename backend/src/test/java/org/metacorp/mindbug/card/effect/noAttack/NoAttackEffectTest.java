package org.metacorp.mindbug.card.effect.noAttack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoAttackEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private NoAttackEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new NoAttackEffect();
    }

    @Test
    public void testWithLowestParameter_noEffect() {
        effect.setLowest(true);
        effect.apply(game, randomCard);
    }

    @Test
    public void testWithLowestParameter_singleCard() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        effect.setLowest(true);
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanAttack());
    }

    @Test
    public void testWithLowestParameter_twoCardsSamePowerAndAnotherHigher() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard3.setPower(otherCard.getPower() + 1);
        opponentPlayer.addCardToBoard(otherCard3);

        effect.setLowest(true);
        effect.apply(game, randomCard);

        assertFalse(otherCard.isCanAttack());
        assertFalse(otherCard2.isCanAttack());
        assertTrue(otherCard3.isCanAttack());
    }

    @Test
    public void testWithLowestParameter_fourCards() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard3.setPower(otherCard.getPower() + 1);
        opponentPlayer.addCardToBoard(otherCard3);

        CardInstance otherCard4 = opponentPlayer.getHand().getFirst();
        otherCard4.setPower(otherCard.getPower() - 1);
        opponentPlayer.addCardToBoard(otherCard4);

        effect.setLowest(true);
        effect.apply(game, randomCard);

        assertTrue(otherCard.isCanAttack());
        assertTrue(otherCard2.isCanAttack());
        assertTrue(otherCard3.isCanAttack());
        assertFalse(otherCard4.isCanAttack());
    }
}
