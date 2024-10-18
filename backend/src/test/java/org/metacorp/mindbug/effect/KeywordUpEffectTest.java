package org.metacorp.mindbug.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Keyword;
import org.metacorp.mindbug.Player;

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
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testWithAloneCondition() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.SNEAKY);
        effect.setAlone(true);

        // Nothing should happen as there are no cards on board
        randomCard.getKeywords().clear();
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add card to board and check effect is correctly applied
        currentPlayer.addCardToBoard(randomCard, false);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.SNEAKY));

        // Add card to board and check effect is correctly applied
        randomCard.getKeywords().clear();
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMoreAllies() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.HUNTER);
        effect.setMoreAllies(true);

        // Nothing should happen as there are no cards on board
        randomCard.getKeywords().clear();
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add card to board and check effect is correctly applied
        currentPlayer.addCardToBoard(randomCard, false);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.HUNTER));

        // Add a card to opponent board and check effect is no more applied
        randomCard.getKeywords().clear();
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add a card to opponent board and check effect is no more applied
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst(), false);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst(), false);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.HUNTER));
    }

    @Test
    public void testWithOpponentHasCondition() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.POISONOUS);
        effect.setOpponentHas(true);

        // Nothing should happen as there are no cards on board
        randomCard.getKeywords().clear();
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add card to board and check effect is correctly applied
        currentPlayer.addCardToBoard(randomCard, false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add a card to opponent board and check effect is no more applied
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.getKeywords().clear();
        opponentPlayer.addCardToBoard(opponentCard, false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add SNEAKY and POISONOUS keywords to opponent card
        opponentCard.getKeywords().add(Keyword.SNEAKY);
        opponentCard.getKeywords().add(Keyword.POISONOUS);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.POISONOUS));

        // Add SNEAKY and POISONOUS keywords to opponent card
        randomCard.getKeywords().clear();
        opponentCard.getKeywords().add(Keyword.SNEAKY);
        opponentCard.getKeywords().add(Keyword.HUNTER);
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.getKeywords().clear();
        otherCard.getKeywords().add(Keyword.POISONOUS);
        otherCard.getKeywords().add(Keyword.FRENZY);
        opponentPlayer.addCardToBoard(otherCard, false);
        effect.apply(game, randomCard);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(Keyword.POISONOUS));
    }

    @Test
    public void testWithMaxCondition() {
        KeywordUpEffect effect = new KeywordUpEffect();
        effect.setValue(Keyword.FRENZY);
        effect.setMax(6);

        // Nothing should happen as there are no cards on board
        randomCard.getKeywords().clear();
        randomCard.setPower(3);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add card to board and check effect is still not applied
        currentPlayer.addCardToBoard(randomCard, false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());

        // Add another card to the board and check effect is correctly applied
        randomCard.reset();
        randomCard.getKeywords().clear();
        CardInstance otherCard = currentPlayer.getHand().getFirst();
        otherCard.setPower(2);
        otherCard.getKeywords().clear();
        currentPlayer.addCardToBoard(otherCard, false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
        assertEquals(1, otherCard.getKeywords().size());
        assertTrue(otherCard.getKeywords().contains(Keyword.FRENZY));

        // Update card power to 8 and check effect is no more applied
        otherCard.getKeywords().clear();
        otherCard.setPower(8);
        currentPlayer.addCardToBoard(randomCard, false);
        effect.apply(game, randomCard);

        assertTrue(randomCard.getKeywords().isEmpty());
        assertTrue(otherCard.getKeywords().isEmpty());
    }
}
