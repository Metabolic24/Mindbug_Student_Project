package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoAttackEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private NoAttackEffect effect;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
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

        assertFalse(otherCard.isAbleToAttack());
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

        assertFalse(otherCard.isAbleToAttack());
        assertFalse(otherCard2.isAbleToAttack());
        assertTrue(otherCard3.isAbleToAttack());
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

        assertTrue(otherCard.isAbleToAttack());
        assertTrue(otherCard2.isAbleToAttack());
        assertTrue(otherCard3.isAbleToAttack());
        assertFalse(otherCard4.isAbleToAttack());
    }
}
