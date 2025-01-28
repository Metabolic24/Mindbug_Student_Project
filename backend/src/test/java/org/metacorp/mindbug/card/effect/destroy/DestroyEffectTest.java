package org.metacorp.mindbug.card.effect.destroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.gainLp.GainEffect;
import org.metacorp.mindbug.card.effect.inflict.InflictEffect;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.player.Player;

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
        randomCard.setStillTough(false);
        currentPlayer.addCardToBoard(randomCard);

        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    //TODO Tester la résolution des choix

    @Test
    public void testLowestAndSelfAllowed_singleCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_noCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_singleOpponentCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        currentPlayer.getBoard().clear();

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_opponentCardLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower()-1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_samePower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower());
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() + 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Current player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestAndSelfAllowed_multipleCards() {
        DestroyEffect effect = new DestroyEffect();
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
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        // Current player card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_noCards() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();

        // Nothing should happen as there are no cards on board
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_singleOpponentCard() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        currentPlayer.getBoard().clear();

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent card should be destroyed as it is the only one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_opponentCardLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() - 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Opponent player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_samePower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower());
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Both cards should be destroyed as they have same power
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setLowest(true);
        effect.setSelfAllowed(false);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setPower(randomCard.getPower() + 1);
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Current player card should be destroyed as it is the lowest one
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLowestButNotSelfAllowed_multipleCards() {
        DestroyEffect effect = new DestroyEffect();
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
        effect.apply(game, randomCard);
        assertEquals(0, currentPlayer.getDiscardPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_moreAllies() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        // Nothing should happen as current player has more allies than the opponent
        effect.apply(game, randomCard);
        assertNull(game.getCurrentChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_sameAlliesCount() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        // Nothing should happen as current player has as much allies as the opponent
        effect.apply(game, randomCard);
        assertNull(game.getCurrentChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getBoard().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueLower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard2);

        // Effect should trigger as current player has less allies than the opponent
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());

        assertNotNull(game.getCurrentChoice());
        assertEquals(ChoiceType.TARGET, game.getCurrentChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getCurrentChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effect, targetChoice.getEffect());
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
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(2);
        effect.setLessAllies(true);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = opponentPlayer.getHand().getFirst();
        otherCard2.setStillTough(false);
        opponentPlayer.addCardToBoard(otherCard2);

        effect.apply(game, randomCard);
        assertNull(game.getCurrentChoice());
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testLessAllies_lessAlliesAndValueHigher() {
        DestroyEffect effect = new DestroyEffect();
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

        effect.apply(game, randomCard);
        assertNull(game.getCurrentChoice());
        assertEquals(2, currentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_lowerPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(2);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should not be destroyed
        effect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_higherPower() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(8);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should not be destroyed
        effect.apply(game, randomCard);
        assertEquals(1, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testMinAndMax_samePowerThanMin() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(3);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_powerInInterval() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(4);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_samePowerThanMax() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setPower(5);
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Card should be destroyed
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());
        assertTrue(opponentPlayer.getDiscardPile().contains(card));
    }

    @Test
    public void testMinAndMax_selfAllowed() {
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);
        effect.setMin(3);
        effect.setMax(5);
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
    public void testMinAndMax_choice() {
        DestroyEffect effect = new DestroyEffect();
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
        effect.apply(game, randomCard);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        assertNotNull(game.getCurrentChoice());
        assertEquals(ChoiceType.TARGET, game.getCurrentChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getCurrentChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effect, targetChoice.getEffect());
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
        DestroyEffect effect = new DestroyEffect();
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
        effect.apply(game, randomCard);
        assertEquals(1, currentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(0, opponentPlayer.getDiscardPile().size());

        assertNotNull(game.getCurrentChoice());
        assertEquals(ChoiceType.TARGET, game.getCurrentChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getCurrentChoice();

        assertEquals(currentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(effect, targetChoice.getEffect());
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
        DestroyEffect effect = new DestroyEffect();
        effect.setValue(1);

        CardInstance card = opponentPlayer.getHand().getFirst();
        card.setStillTough(false);
        opponentPlayer.addCardToBoard(card);

        // Check that card is destroyed and that its DEFEATED effects (if any) are added to the effect queue
        effect.apply(game, randomCard);
        assertNull(game.getCurrentChoice());
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(1, opponentPlayer.getDiscardPile().size());

        if (!randomCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
            assertEquals(randomCard.getEffects(EffectTiming.DEFEATED).size(), game.getEffectQueue().size());
        }
    }

    @Test
    public void testDestroyCards_multiple() {
        DestroyEffect effect = new DestroyEffect();
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
        effect.apply(game, randomCard);
        assertEquals(0, opponentPlayer.getBoard().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
        assertEquals(2, game.getEffectQueue().size());
    }
}
