package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoBlockEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class NoBlockEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private NoBlockEffect effect;
    private NoBlockEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new NoBlockEffect();
        effectResolver = new NoBlockEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    //TODO Test choice resolution

    @Test
    public void testBasic_lessThanBoardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard);
        CardInstance secondCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(secondCard);

        effect.setValue(1);
        effectResolver.apply(game, randomCard, timing);

        assertTrue(firstCard.isAbleToBlock());
        assertTrue(secondCard.isAbleToBlock());

        assertNotNull(game.getChoice());

        TargetChoice targetChoice = (TargetChoice) game.getChoice();
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(effectResolver, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(game.getCurrentPlayer(), targetChoice.getPlayerToChoose());
        assertEquals(2, targetChoice.getAvailableTargets().size());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(card::equals).count());
        }
    }

    @Test
    public void testBasic_boardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard);
        CardInstance secondCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(secondCard);

        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertFalse(firstCard.isAbleToBlock());
        assertFalse(secondCard.isAbleToBlock());

        assertNull(game.getChoice());
    }

    @Test
    public void testBasic_moreThanBoardSize() {
        CardInstance firstCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(firstCard);

        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertFalse(firstCard.isAbleToBlock());
        assertNull(game.getChoice());
    }

    @Test
    public void testWithMaxCondition_noEffect() {
        effect.setMax(5);
        effectResolver.apply(game, randomCard, timing);
    }

    @Test
    public void testWithMaxCondition_singleMatchingCardSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        effect.setMax(otherCard.getPower());
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
    }

    @Test
    public void testWithMaxCondition_twoMatchingCardSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2);

        effect.setMax(otherCard.getPower());
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
        assertFalse(otherCard2.isAbleToBlock());
    }

    @Test
    public void testWithMaxCondition_twoMatchingCardDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 1);
        opponentPlayer.addCardToBoard(otherCard2);

        effect.setMax(otherCard.getPower());
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
        assertFalse(otherCard2.isAbleToBlock());
    }

    @Test
    public void testWithMaxCondition_twoCardsOneMatching() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() + 1);
        opponentPlayer.addCardToBoard(otherCard2);

        effect.setMax(otherCard.getPower());
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
        assertTrue(otherCard2.isAbleToBlock());
    }

    @Test
    public void testWithHighestParameter_singleCard() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        effect.setHighest(true);
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
    }

    @Test
    public void testWithHighestParameter_twoCardsSamePower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower());
        opponentPlayer.addCardToBoard(otherCard2);

        effect.setHighest(true);
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
        assertFalse(otherCard2.isAbleToBlock());
    }

    @Test
    public void testWithHighestParameter_twoCardsDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 2);
        opponentPlayer.addCardToBoard(otherCard2);

        effect.setHighest(true);
        effectResolver.apply(game, randomCard, timing);

        assertFalse(otherCard.isAbleToBlock());
        assertTrue(otherCard2.isAbleToBlock());
    }

    @Test
    public void testWithHighestParameter_threeCardsDifferentPower() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setPower(otherCard.getPower() - 2);
        opponentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard3.setPower(otherCard.getPower() + 4);
        opponentPlayer.addCardToBoard(otherCard3);

        effect.setHighest(true);
        effectResolver.apply(game, randomCard, timing);

        assertTrue(otherCard.isAbleToBlock());
        assertTrue(otherCard2.isAbleToBlock());
        assertFalse(otherCard3.isAbleToBlock());
    }
}
