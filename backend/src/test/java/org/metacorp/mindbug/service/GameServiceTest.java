package org.metacorp.mindbug.service;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameServiceTest extends MindbugGameTest {

    private Game game;
    private GameService gameService;

    @BeforeEach
    public void initGame() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        PlayerService playerService = locator.getService(PlayerService.class);
        gameService = locator.getService(GameService.class);
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
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
    public void endGame_nominal() throws UnknownPlayerException, WebSocketException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        UUID winnerId = players.get(1).getUuid();
        game = gameService.createGame(loserId, winnerId);

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinner().getUuid());
    }

    @Test
    public void endGame_badGame() throws WebSocketException {
        gameService.endGame(UUID.randomUUID(), UUID.randomUUID());
        assertNull(game.getWinner());
    }

    @Test
    public void endGame_badPlayer() throws UnknownPlayerException, WebSocketException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        game = gameService.createGame(loserId, players.get(1).getUuid());

        gameService.endGame(UUID.randomUUID(), game.getUuid());
        assertNull(game.getWinner());
    }
}
