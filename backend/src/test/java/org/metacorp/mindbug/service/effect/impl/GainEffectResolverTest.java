package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GainEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private GainEffect effect;
    private GainEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new GainEffect();
        effectResolver = new GainEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic() {
        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(5, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_noEffect() {
        effect.setEqual(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_moreLifePoints() {
        opponentPlayer.getTeam().setLifePoints(5);

        effect.setEqual(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_lessLifePoints() {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setEqual(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }
}
