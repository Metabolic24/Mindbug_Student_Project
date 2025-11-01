package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InflictEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private InflictEffect effect;
    private InflictEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        randomCard = currentPlayer.getHand().getFirst();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new InflictEffect();
        effectResolver = new InflictEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic() {
        effect.setValue(2);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithSelfParameter() {
        effect.setValue(4);
        effect.setSelf(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(0, currentPlayer.getTeam().getLifePoints());
        assertEquals(3, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_nominal() {
        effect.setAllButOne(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_noEffect() {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setAllButOne(true);
        effectResolver.apply(game, randomCard, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }
}
