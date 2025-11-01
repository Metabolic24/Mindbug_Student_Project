package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrawEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;

    private DrawEffect effect;
    private DrawEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        randomCard = currentPlayer.getHand().getFirst();

        effect = new DrawEffect();
        effectResolver = new DrawEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic_draw2Over5() {
        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(7, currentPlayer.getHand().size());
        assertEquals(3, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testBasic_draw4Over3() {
        currentPlayer.drawX(2);
        assertEquals(7, currentPlayer.getHand().size());
        assertEquals(3, currentPlayer.getDrawPile().size());

        effect.setValue(4);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());
    }

    @Test
    public void testBasic_draw2Over0() {
        currentPlayer.drawX(5);
        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());

        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(10, currentPlayer.getHand().size());
        assertEquals(0, currentPlayer.getDrawPile().size());
    }
}
