package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InflictEffectResolverTest {

    private Game game;
    private Player currentPlayer;
    private Player opponentPlayer;

    private InflictEffect effect;
    private InflictEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        CardInstance randomCard = currentPlayer.getHand().getFirst();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new InflictEffect();
        effect.setType(EffectType.INFLICT);
        effectResolver = new InflictEffectResolver(effect, randomCard);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic() throws WebSocketException {
        effect.setValue(2);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithSelfParameter() throws WebSocketException {
        effect.setValue(4);
        effect.setSelf(true);
        effectResolver.apply(game, timing);

        assertEquals(0, currentPlayer.getTeam().getLifePoints());
        assertEquals(3, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_nominal() throws WebSocketException {
        effect.setAllButOne(true);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithAllButOneParameter_noEffect() throws WebSocketException {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setAllButOne(true);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithMindbugCount_nominal() throws WebSocketException {
        opponentPlayer.setMindBugs(2);
        opponentPlayer.getTeam().setLifePoints(3);

        effect.setMindbugCount(true);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithMindbugCount_noEffect() throws WebSocketException {
        opponentPlayer.setMindBugs(0);
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setMindbugCount(true);
        effectResolver.apply(game, timing);

        assertEquals(1, opponentPlayer.getTeam().getLifePoints());
    }
}
