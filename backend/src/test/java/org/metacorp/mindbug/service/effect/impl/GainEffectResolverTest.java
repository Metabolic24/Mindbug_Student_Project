package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.utils.MindbugGameTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GainEffectResolverTest extends MindbugGameTest {

    private Game game;
    private Player currentPlayer;
    private Player opponentPlayer;

    private GainEffect effect;
    private GainEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        CardInstance randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        effect = new GainEffect();
        effect.setType(EffectType.GAIN);
        effectResolver = new GainEffectResolver(effect, randomCard);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic() throws WebSocketException {
        effect.setValue(2);
        effectResolver.apply(game, timing);

        assertEquals(5, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_noEffect() throws WebSocketException {
        effect.setEqual(true);
        effectResolver.apply(game, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_moreLifePoints() throws WebSocketException {
        opponentPlayer.getTeam().setLifePoints(5);

        effect.setEqual(true);
        effectResolver.apply(game, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testWithEqualParameter_lessLifePoints() throws WebSocketException {
        opponentPlayer.getTeam().setLifePoints(1);

        effect.setEqual(true);
        effectResolver.apply(game, timing);

        assertEquals(opponentPlayer.getTeam().getLifePoints(), currentPlayer.getTeam().getLifePoints());
    }
}
