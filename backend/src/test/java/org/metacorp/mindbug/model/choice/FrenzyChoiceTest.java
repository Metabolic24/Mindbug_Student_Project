package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrenzyChoiceTest {
    private Game game;
    private Player currentPlayer;
    private CardInstance currentCard;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        currentCard = currentPlayer.getHand().getFirst();
        currentCard.getCard().setKeywords(new HashSet<>(List.of(CardKeyword.FRENZY)));
        currentCard.setKeywords(new HashSet<>(List.of(CardKeyword.FRENZY)));
        currentCard.getEffects(EffectTiming.ATTACK).clear();
        currentCard.setAbleToAttackTwice(true);
        currentPlayer.addCardToBoard(currentCard);

        game.setAttackingCard(null);
    }

    @Test
    public void testResolve_trueWithEmptyBoard() throws GameStateException {
        Player opponent = game.getOpponent().getFirst();

        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(true, game);

        assertNull(game.getChoice());
        assertTrue(currentCard.isAbleToAttackTwice());
        assertNull(game.getAttackingCard());

        assertEquals(game.getCurrentPlayer(), opponent);
        assertEquals(2, opponent.getTeam().getLifePoints());
    }

    @Test
    public void testResolve_trueWithOpponentCreatures() throws GameStateException {
        Player opponent = game.getOpponent().getFirst();;
        opponent.addCardToBoard(opponent.getHand().getFirst());

        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(true, game);

        assertNull(game.getChoice());
        assertFalse(currentCard.isAbleToAttackTwice());
        assertEquals(currentCard, game.getAttackingCard());
    }

    @Test
    public void testResolve_false() throws GameStateException {
        FrenzyAttackChoice choice = new FrenzyAttackChoice(currentCard);
        game.setChoice(choice);

        choice.resolve(false, game);

        assertNull(game.getChoice());
        assertEquals(currentPlayer.getOpponent(game.getPlayers()).getFirst(), game.getCurrentPlayer());
        assertTrue(currentCard.isAbleToAttackTwice());
    }
}
