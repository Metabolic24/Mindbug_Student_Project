package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private DiscardEffect effect;
    private DiscardEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        effect = new DiscardEffect();
        effect.setType(EffectType.DISCARD);
        effect.setValue(3);
        effectResolver = new DiscardEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    //TODO Test choice resolution

    @Test
    public void testBasic_opponentHandIs2() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard, timing);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(2, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs3() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard, timing);
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testBasic_opponentHandIs5() {
        effectResolver.apply(game, randomCard, timing);

        assertEquals(5, opponentPlayer.getHand().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());

        TargetChoice targetChoice = assertInstanceOf(TargetChoice.class, game.getChoice());
        assertEquals(ChoiceType.TARGET, targetChoice.getType());
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

    @Test
    public void testEachEnemy_nominal() {
        effect.setEachEnemy(true);

        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        opponentPlayer.drawX(1);

        effectResolver.apply(game, randomCard, timing);

        assertNull(game.getChoice());
        assertTrue(opponentPlayer.getHand().isEmpty());
        assertEquals(3, opponentPlayer.getDiscardPile().size());
    }

    @Test
    public void testSelf_nominal() {
        effect.setValue(-1);
        effect.setSelf(true);

        effectResolver.apply(game, randomCard, timing);

        assertNull(game.getChoice());
        assertEquals(5, opponentPlayer.getHand().size());
        assertTrue(opponentPlayer.getDiscardPile().isEmpty());
        assertTrue(game.getCurrentPlayer().getHand().isEmpty());
        assertEquals(5, game.getCurrentPlayer().getDiscardPile().size());
    }

    @Test
    public void testDrawPile_nominal() {
        effect.setValue(2);
        effect.setDrawPile(true);

        CardInstance firstCard = opponentPlayer.getDrawPile().get(0);
        CardInstance secondCard = opponentPlayer.getDrawPile().get(1);
        CardInstance thirdCard = opponentPlayer.getDrawPile().get(2);

        effectResolver.apply(game, randomCard, timing);

        assertNull(game.getChoice());
        assertEquals(5, opponentPlayer.getHand().size());
        assertEquals(3, opponentPlayer.getDrawPile().size());
        assertEquals(2, opponentPlayer.getDiscardPile().size());

        assertTrue(opponentPlayer.getDiscardPile().contains(firstCard));
        assertTrue(opponentPlayer.getDiscardPile().contains(secondCard));
        assertFalse(opponentPlayer.getDiscardPile().contains(thirdCard));
    }
}
