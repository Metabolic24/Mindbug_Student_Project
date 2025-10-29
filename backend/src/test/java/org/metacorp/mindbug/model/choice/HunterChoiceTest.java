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
import org.metacorp.mindbug.service.StartService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HunterChoiceTest {

    private Game game;
    private Player currentPlayer;
    private Player opponent;
    private CardInstance currentCard;
    private CardInstance opponentCard;
    private CardInstance opponentCard2;

    @BeforeEach
    public void initGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponent = game.getOpponent();

        currentCard = currentPlayer.getHand().getFirst();
        currentCard.getCard().setKeywords(new HashSet<>(List.of(CardKeyword.HUNTER)));
        currentCard.setAbleToAttackTwice(false);
        currentCard.setKeywords(new HashSet<>(List.of(CardKeyword.HUNTER)));
        currentCard.setPower(10);
        currentPlayer.addCardToBoard(currentCard);

        opponentCard = opponent.getHand().getFirst();
        opponentCard.setKeywords(new HashSet<>());
        opponentCard.getCard().setKeywords(new HashSet<>());
        opponentCard.setStillTough(false);
        opponentCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>());
        opponentCard.setPower(1);
        opponent.addCardToBoard(opponentCard);

        opponentCard2 = opponent.getHand().getFirst();
        opponent.addCardToBoard(opponentCard2);

        game.setAttackingCard(currentCard);
    }

    @Test
    public void testResolve_ignoreHunter() throws GameStateException {
        HunterChoice choice = new HunterChoice(currentPlayer, currentCard, new HashSet<>(opponent.getBoard()));
        game.setChoice(choice);

        choice.resolve(null, game);

        assertTrue(currentPlayer.getBoard().contains(currentCard));
        assertTrue(opponent.getBoard().contains(opponentCard));
        assertTrue(opponent.getBoard().contains(opponentCard2));
        assertNull(game.getChoice());
        assertNotNull(game.getAttackingCard());
    }

    @Test
    public void testResolve_nominal() throws GameStateException {
        HunterChoice choice = new HunterChoice(currentPlayer, currentCard, new HashSet<>(opponent.getBoard()));
        game.setChoice(choice);

        choice.resolve(opponentCard.getUuid(), game);

        assertTrue(currentPlayer.getBoard().contains(currentCard));
        assertTrue(opponent.getDiscardPile().contains(opponentCard));
        assertTrue(opponent.getBoard().contains(opponentCard2));
        assertNull(game.getChoice());
        assertNull(game.getAttackingCard());
        assertEquals(game.getCurrentPlayer(), opponent);
    }
}
