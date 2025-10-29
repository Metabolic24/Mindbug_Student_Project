package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.KeywordUpEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeywordUpEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private KeywordUpEffect effect;
    private KeywordUpEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.getKeywords().clear();
        currentPlayer.addCardToBoard(randomCard);

        effect = new KeywordUpEffect();
        effectResolver = new KeywordUpEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testWithAloneCondition_singleCard() {
        effect.setValue(CardKeyword.SNEAKY);
        effect.setAlone(true);

        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(CardKeyword.SNEAKY));
    }

    @Test
    public void testWithAloneCondition_multipleCards() {
        effect.setValue(CardKeyword.SNEAKY);
        effect.setAlone(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMoreAllies_noOpponentCard() {
        effect.setValue(CardKeyword.HUNTER);
        effect.setMoreAllies(true);

        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(CardKeyword.HUNTER));
    }

    @Test
    public void testWithMoreAllies_sameCardsCount() {
        effect.setValue(CardKeyword.HUNTER);
        effect.setMoreAllies(true);

        // Add a card to opponent board and check effect is no more applied
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMoreAllies_moreThanOpponent() {
        effect.setValue(CardKeyword.HUNTER);
        effect.setMoreAllies(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(CardKeyword.HUNTER));
    }

    @Test
    public void testWithMoreAllies_lessThanOpponent() {
        effect.setValue(CardKeyword.HUNTER);
        effect.setMoreAllies(true);

        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_noOpponentCard() {
        effect.setValue(CardKeyword.POISONOUS);
        effect.setOpponentHas(true);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithNoKeyword() {
        effect.setValue(CardKeyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add a card with no keywords to opponent board and check effect is not applied
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.getKeywords().clear();
        opponentPlayer.addCardToBoard(opponentCard);
        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithMatchingKeyword() {
        effect.setValue(CardKeyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.SNEAKY, CardKeyword.POISONOUS)));
        opponentPlayer.addCardToBoard(opponentCard);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(CardKeyword.POISONOUS));
    }

    @Test
    public void testWithOpponentHasCondition_singleCardWithNoMatchingKeyword() {
        effect.setValue(CardKeyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.SNEAKY, CardKeyword.HUNTER)));
        opponentPlayer.addCardToBoard(opponentCard);
        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithOpponentHasCondition_multipleCardsWithOneMatchingKeyword() {
        effect.setValue(CardKeyword.POISONOUS);
        effect.setOpponentHas(true);

        // Add SNEAKY and POISONOUS keywords to opponent card
        CardInstance opponentCard = opponentPlayer.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.SNEAKY, CardKeyword.HUNTER)));
        opponentPlayer.addCardToBoard(opponentCard);

        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.POISONOUS, CardKeyword.FRENZY)));
        opponentPlayer.addCardToBoard(otherCard);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, randomCard.getKeywords().size());
        assertTrue(randomCard.getKeywords().contains(CardKeyword.POISONOUS));
    }

    @Test
    public void testWithMaxCondition_singleCardOnBoard() {
        effect.setValue(CardKeyword.FRENZY);
        effect.setMax(6);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
    }

    @Test
    public void testWithMaxCondition_multipleCards() {
        effect.setValue(CardKeyword.FRENZY);
        effect.setMax(6);

        CardInstance otherCard = currentPlayer.getHand().getFirst();
        otherCard.setPower(4);
        otherCard.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.SNEAKY, CardKeyword.HUNTER)));
        currentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        otherCard2.setPower(7);
        otherCard2.setKeywords(new HashSet<>(Arrays.asList(CardKeyword.POISONOUS, CardKeyword.FRENZY)));
        currentPlayer.addCardToBoard(otherCard2);

        CardInstance otherCard3 = currentPlayer.getHand().getFirst();
        otherCard3.setPower(6);
        otherCard3.getKeywords().clear();
        currentPlayer.addCardToBoard(otherCard3);

        CardInstance otherCard4 = currentPlayer.getHand().getFirst();
        otherCard4.setPower(9);
        otherCard4.getKeywords().clear();
        currentPlayer.addCardToBoard(otherCard4);

        effectResolver.apply(game, randomCard, timing);

        assertTrue(randomCard.getKeywords().isEmpty());
        assertEquals(3, otherCard.getKeywords().size());
        assertTrue(otherCard.getKeywords().contains(CardKeyword.FRENZY));
        assertEquals(2, otherCard2.getKeywords().size());
        assertTrue(otherCard2.getKeywords().contains(CardKeyword.FRENZY));
        assertEquals(1, otherCard3.getKeywords().size());
        assertTrue(otherCard3.getKeywords().contains(CardKeyword.FRENZY));
        assertTrue(otherCard4.getKeywords().isEmpty());
    }
}
