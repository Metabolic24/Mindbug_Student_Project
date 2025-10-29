package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoAttackEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoAttackEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private NoAttackEffect effect;
    private NoAttackEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new NoAttackEffect();
        effectResolver = new NoAttackEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testWithLowestParameter_noEffect() {
        effect.setLowest(true);
        effectResolver.apply(game, randomCard, timing);
    }

    @Test
    public void testWithLowestParameter_singleCard() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        effect.setLowest(true);
        effectResolver.apply(game, randomCard, timing);

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
        effectResolver.apply(game, randomCard, timing);

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
        effectResolver.apply(game, randomCard, timing);

        assertTrue(otherCard.isAbleToAttack());
        assertTrue(otherCard2.isAbleToAttack());
        assertTrue(otherCard3.isAbleToAttack());
        assertFalse(otherCard4.isAbleToAttack());
    }
}
