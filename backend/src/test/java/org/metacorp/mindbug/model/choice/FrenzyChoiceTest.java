package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrenzyChoiceTest extends MindbugGameTest {

    private Game game;
    private Player currentPlayer;
    private CardInstance currentCard;

    @BeforeEach
    public void initGame() throws CardSetException {
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));

        currentPlayer = game.getCurrentPlayer();
        currentCard = currentPlayer.getHand().getFirst();
        currentCard.getCard().setKeywords(new HashSet<>(List.of(CardKeyword.FRENZY)));
        currentCard.setKeywords(new HashSet<>(List.of(CardKeyword.FRENZY)));
        currentCard.getEffects(EffectTiming.ATTACK).clear();
        currentCard.setAbleToAttackTwice(true);
        currentPlayer.addCardToBoard(currentCard);
    }

    @Test
    public void testResolve_trueWithEmptyBoard() throws GameStateException, WebSocketException {
        Player opponent = game.getOpponents().getFirst();

        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(true, game);

        assertNull(game.getChoice());
        assertTrue(currentCard.isAbleToAttackTwice());

        assertEquals(game.getCurrentPlayer(), opponent);
        assertEquals(2, opponent.getTeam().getLifePoints());
    }

    @Test
    public void testResolve_trueWithOpponentCreatures() throws GameStateException, WebSocketException {
        Player opponent = game.getOpponents().getFirst();;
        opponent.addCardToBoard(opponent.getHand().getFirst());

        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(true, game);

        BlockChoice blockChoice = assertInstanceOf(BlockChoice.class, game.getChoice());
        assertFalse(currentCard.isAbleToAttackTwice());
        assertEquals(currentCard, blockChoice.getAttackingCard());
    }

    @Test
    public void testResolve_false() throws GameStateException, WebSocketException {
        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(false, game);

        assertNull(game.getChoice());
        assertEquals(currentPlayer.getOpponents(game.getPlayers()).getFirst(), game.getCurrentPlayer());
        assertTrue(currentCard.isAbleToAttackTwice());
    }
}
