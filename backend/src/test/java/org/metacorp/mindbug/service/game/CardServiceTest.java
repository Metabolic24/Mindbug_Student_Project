package org.metacorp.mindbug.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardServiceTest {

    private Game game;
    private Player currentPlayer;

    @BeforeEach
    public void initGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
    }

    @Test
    public void defeatCard_tough() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(boardCard);
        boardCard.getKeywords().add(CardKeyword.TOUGH);
        boardCard.setStillTough(true);

        CardService.defeatCard(boardCard, game.getEffectQueue());
        assertFalse(boardCard.isStillTough());
        assertTrue(currentPlayer.getBoard().contains(boardCard));
        assertFalse(currentPlayer.getDiscardPile().contains(boardCard));
        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void defeatCard_noMoreTough() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(boardCard);
        boardCard.getKeywords().add(CardKeyword.TOUGH);
        boardCard.setStillTough(false);
        boardCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(List.of(new GainEffect())));

        CardService.defeatCard(boardCard, game.getEffectQueue());
        assertFalse(boardCard.isStillTough());
        assertFalse(currentPlayer.getBoard().contains(boardCard));
        assertTrue(currentPlayer.getDiscardPile().contains(boardCard));

        assertEquals(1, game.getEffectQueue().size());
        assertEquals(boardCard, game.getEffectQueue().getFirst().getCard());
        assertEquals(1, game.getEffectQueue().getFirst().getEffects().size());
        assertInstanceOf(GainEffect.class, game.getEffectQueue().getFirst().getEffects().getFirst());
    }
}
