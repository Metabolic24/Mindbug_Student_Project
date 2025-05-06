package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.StartService;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private DiscardEffect effect;
    private DiscardEffectResolver effectResolver;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player("Player1"), new Player("Player2"));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new DiscardEffect();
        effect.setValue(3);
        effectResolver = new DiscardEffectResolver(effect);
    }

    //TODO Test choice resolution

    @Test
    public void testBasic_opponentHandIs2() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs3() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs5() {
        effectResolver.apply(game, randomCard);

        assertEquals(5, opponentPlayer.getHand().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertEquals(3, targetChoice.getTargetsCount());
        assertEquals(effectResolver, targetChoice.getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(opponentPlayer, targetChoice.getPlayerToChoose());
        assertEquals(5, targetChoice.getAvailableTargets().size());

        for (CardInstance card : opponentPlayer.getHand()) {
            assertEquals(1, targetChoice.getAvailableTargets().stream()
                    .filter(card::equals).count());
        }
    }
}
