package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestroyEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private DestroyEffect effect;
    private DestroyEffectResolver effectResolver;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.setStillTough(false);
        currentPlayer.addCardToBoard(randomCard);

        effect = new DestroyEffect();
        effectResolver = new DestroyEffectResolver(effect);
    }

    //TODO Test choice resolution

    @Test
    public void testLowestAndSelfAllowed_singleCard() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        // Current player card should be destroyed as it is the only one
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_noCards() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_singleOpponentCard() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent card should be destroyed as it is the only one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_opponentCardLower() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower()-1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent player card should be destroyed as it is the lowest one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_samePower() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower());
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Both cards should be destroyed as they have same power
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_higherPower() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() + 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Current player card should be destroyed as it is the lowest one
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_multipleCards() {
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance currentPlayerCard1 = currentPlayer.getHand().getFirst();
        currentPlayerCard1.setPower(randomCard.getPower() + 4);
        currentPlayerCard1.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard1);

        CardInstance currentPlayerCard2 = currentPlayer.getHand().getFirst();
        currentPlayerCard2.setPower(randomCard.getPower() - 1);
        currentPlayerCard2.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard2);

        CardInstance currentPlayerCard3 = currentPlayer.getHand().getFirst();
        currentPlayerCard3.setPower(randomCard.getPower() - 2);
        currentPlayerCard3.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard3);

        CardInstance opponentPlayerCard1 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard1.setPower(randomCard.getPower() - 2);
        opponentPlayerCard1.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard1);

        CardInstance opponentPlayerCard2 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard2.setPower(randomCard.getPower() + 1);
        opponentPlayerCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard2);

        CardInstance opponentPlayerCard3 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard3.setPower(randomCard.getPower() - 2);
        opponentPlayerCard3.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard3);

        // Both cards should be destroyed as they have same power
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleCard() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        // Current player card should be destroyed as it is the only one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_noCards() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleOpponentCard() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent card should be destroyed as it is the only one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_opponentCardLower() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() - 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent player card should be destroyed as it is the lowest one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_samePower() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower());
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Both cards should be destroyed as they have same power
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_higherPower() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() + 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Current player card should be destroyed as it is the lowest one
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_multipleCards() {
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance currentPlayerCard1 = currentPlayer.getHand().getFirst();
        currentPlayerCard1.setPower(randomCard.getPower() + 4);
        currentPlayerCard1.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard1);

        CardInstance currentPlayerCard2 = currentPlayer.getHand().getFirst();
        currentPlayerCard2.setPower(randomCard.getPower() - 1);
        currentPlayerCard2.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard2);

        CardInstance currentPlayerCard3 = currentPlayer.getHand().getFirst();
        currentPlayerCard3.setPower(randomCard.getPower() - 2);
        currentPlayerCard3.setStillTough(false);
        currentPlayer.addCardToBoard(currentPlayerCard3);

        CardInstance opponentPlayerCard1 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard1.setPower(randomCard.getPower() - 2);
        opponentPlayerCard1.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard1);

        CardInstance opponentPlayerCard2 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard2.setPower(randomCard.getPower() + 1);
        opponentPlayerCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard2);

        CardInstance opponentPlayerCard3 = opponentPlayer.getHand().getFirst();
        opponentPlayerCard3.setPower(randomCard.getPower() - 2);
        opponentPlayerCard3.setStillTough(false);
        opponentPlayer.addCardToBoard(opponentPlayerCard3);

        // Both cards should be destroyed as they have same power
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_moreAllies() {
        effect.setValue(1);
        effect.setLessAllies(true);

        // Nothing should happen as current player has more allies than the opponent
        effectResolver.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_sameAlliesCount() {
        effect.setValue(1);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Nothing should happen as current player has as much allies as the opponent
        effectResolver.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueLower() {
        effect.setValue(1);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard2);

        // Effect should trigger as current player has less allies than the opponent
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effectResolver, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(2, targetChoice.getAvailableTargets().size());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(card::equals)
                    .count());
        }
    }

    @Test
    public void testLessAllies_lessAlliesAndValueSame() {
        effect.setValue(2);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard2);

        effectResolver.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueHigher() {
        effect.setValue(4);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = opponentPlayer.getHand().getFirst();
        otherCard3.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard3);

        CardInstance otherCard4 = currentPlayer.getHand().getFirst();
        otherCard4.setStillTough(false);
        currentPlayer.addCardToBoard(otherCard4);

        effectResolver.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(2, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_lowerPower() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(2);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should not be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_higherPower() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(8);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should not be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_samePowerThanMin() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_powerInInterval() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(4);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_samePowerThanMax() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(5);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_selfAllowed() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);
        effect.setSelfAllowed(true);

        randomCard.setPower(5);

        // Card should be destroyed
        effectResolver.apply(game, randomCard);
        assertEquals(0, currentPlayer.getBoard().size());
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
        assertTrue(currentPlayer.getDiscardPile().contains(randomCard));
    }

    @Test
    public void testMinAndMax_choice() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Cards should not be destroyed but a choice should be created
        effectResolver.apply(game, randomCard);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effectResolver, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(2, targetChoice.getAvailableTargets().size());

        for (CardInstance opponentCard : opponentPlayer.getBoard()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(opponentCard::equals).count());
        }
    }

    @Test
    public void testMinAndMax_choiceAndSelfAllowed() {
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        randomCard.setPower(4);
        effect.setSelfAllowed(true);

        // Card should be destroyed but a choice should be created
        effectResolver.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effectResolver, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(3, targetChoice.getAvailableTargets().size());

        for (CardInstance opponentCard : opponentPlayer.getBoard()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(opponentCard::equals).count());
        }

        assertEquals(1, targetChoice.getAvailableTargets().stream()
                .filter(randomCard::equals).count());
    }

    @Test
    public void testDestroyCards_basic() {
        effect.setValue(1);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Check that card is destroyed and that its DEFEATED effects (if any) are added to the effect queue
        effectResolver.apply(game, randomCard);
        assertNull(game.getChoice());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        if (!randomCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
            assertEquals(randomCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        }
    }

    @Test
    public void testDestroyCards_multiple() {
        effect.setValue(2);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setStillTough(false);
        card.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new GainEffect()));
        opponentPlayer.addCardToBoard(card);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        otherCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(new InflictEffect()));
        opponentPlayer.addCardToBoard(otherCard);

        // Check that cards are destroyed and that a simultaneous choice is created for the cards effects
        effectResolver.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
        assertEquals(2, game.getEffectQueue().size());
    }
}
