package org.metacorp.mindbug.card.effect.keywordUp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.player.Player;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeywordUpEffectTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    @BeforeEach
    public void prepareGame() {
        game = new Game("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        randomCard = game.getCurrentPlayer().getHand().getFirst();
        randomCard.getKeywords().clear();
        currentPlayer.addCardToBoard(randomCard);
    }

    @Test
    public void testWithAloneCondition_singleCard() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.SNEAKY);
        effect.setAlone(true);

        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.SNEAKY));
    }

    @Test
    public void testWithAloneCondition_multipleCards() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.SNEAKY);
        effect.setAlone(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMoreAllies_noOpponentCard() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.HUNTER);
        effect.setMoreAllies(true);

        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.HUNTER));
    }

    @Test
    public void testWithMoreAllies_sameCardsCount() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.HUNTER);
        effect.setMoreAllies(true);

        // Add a card to opponent board and check effect is no more applied
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMoreAllies_moreThanOpponent() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.HUNTER);
        effect.setMoreAllies(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.HUNTER));
    }

    @Test
    public void testWithMoreAllies_lessThanOpponent() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.HUNTER);
        effect.setMoreAllies(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_noOpponentCard() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithNoKeyword() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add a card with no keywords to opponent board and check effect is not applied
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.getKeywords().clear();
        opponentPlayer.addCardToBoard(opponentCard);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithMatchingKeyword() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(Keyword.SNEAKY, Keyword.POISONOUS)));
        opponentPlayer.addCardToBoard(opponentCard);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.POISONOUS));
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithNoMatchingKeyword() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(Keyword.SNEAKY, Keyword.HUNTER)));
        opponentPlayer.addCardToBoard(opponentCard);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_multipleCardsWithOneMatchingKeyword() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(Keyword.SNEAKY, Keyword.HUNTER)));
        opponentPlayer.addCardToBoard(opponentCard);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setKeywords(new HashSet<>(Arrays.asList(Keyword.POISONOUS, Keyword.FRENZY)));
        opponentPlayer.addCardToBoard(otherCard);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.POISONOUS));
    }

    @Test
    public void testWithMaxCondition_singleCardOnBoard() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.FRENZY);
        effect.setMax(6);

        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMaxCondition_multipleCards() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.FRENZY);
        effect.setMax(6);

        CardInstance otherCard = currentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        otherCard.setKeywords(new HashSet<>(Arrays.asList(Keyword.SNEAKY, Keyword.HUNTER)));
        currentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        otherCard2.setPower(7);
        otherCard2.setKeywords(new HashSet<>(Arrays.asList(Keyword.POISONOUS, Keyword.FRENZY)));
        currentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = currentPlayer.getHand().getFirst();
        otherCard3.setPower(6);
        otherCard3.getKeywords().clear();
        currentPlayer.addCardToBoard(otherCard3);

        CardInstance otherCard4 = currentPlayer.getHand().getFirst();
        otherCard4.setPower(9);
        otherCard4.getKeywords().clear();
        currentPlayer.addCardToBoard(otherCard4);

        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
        assertEquals(3, otherCard.getKeywords().size());
        assertTrue(otherCard.getKeywords().contains(Keyword.FRENZY));
        assertEquals(2, otherCard2.getKeywords().size());
        assertTrue(otherCard2.getKeywords().contains(Keyword.FRENZY));
        assertEquals(1, otherCard3.getKeywords().size());
        assertTrue(otherCard3.getKeywords().contains(Keyword.FRENZY));
        assertTrue(otherCard4.getKeywords().isEmpty());
    }
}
