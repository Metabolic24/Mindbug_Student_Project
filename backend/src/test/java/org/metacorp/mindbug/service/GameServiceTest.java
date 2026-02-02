package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.StartService;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private Game game;
    private GameService gameService;

    @BeforeEach
    public void initGame() {
        gameService = new GameService();
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
    }

    @Test
    public void createGame_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        game = gameService.createGame(players.get(0).getUuid(), players.get(1).getUuid());

        assertNotNull(game);
        assertNotNull(game.getUuid());
    }

    @Test
    public void findById_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        game = gameService.createGame(players.get(0).getUuid(), players.get(1).getUuid());

        assertEquals(game, gameService.findById(game.getUuid()));
    }

    @Test
    public void findById_badGame() {
        assertNull(gameService.findById(UUID.randomUUID()));
    }

    @Test
    public void endGame_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        UUID winnerId = players.get(1).getUuid();
        game = gameService.createGame(loserId, winnerId);

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinner().get(0).getUuid());
    }

    @Test
    public void endGame_badGame() {
        gameService.endGame(UUID.randomUUID(), UUID.randomUUID());
        assertNull(game.getWinner());
    }

    @Test
    public void endGame_badPlayer() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        game = gameService.createGame(loserId, players.get(1).getUuid());

        gameService.endGame(UUID.randomUUID(), game.getUuid());
        assertNull(game.getWinner());
    }
}
