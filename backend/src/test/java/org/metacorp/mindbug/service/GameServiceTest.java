package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceTest extends MindbugGameTest {

    private Game game;

    @BeforeEach
    public void initGame() throws CardSetException, UnknownPlayerException {
        PlayerLightDTO player1 = playerService.createPlayer("Player1");
        PlayerLightDTO player2 = playerService.createPlayer("Player2");

        game = createGame(player1.getUuid(), player2.getUuid());
    }

    @Test
    public void createGame_nominal() {
        assertNotNull(game);
        assertNotNull(game.getUuid());
    }

    @Test
    public void createGame_withAi() throws CardSetException, UnknownPlayerException {
        Game otherGame = createGame(game.getCurrentPlayer().getUuid(), null);

        assertNotNull(otherGame);
        assertNotNull(otherGame.getUuid());

        Player aiPlayer = otherGame.getCurrentPlayer().getUuid().equals(game.getCurrentPlayer().getUuid()) ?
                otherGame.getOpponents().getFirst() : otherGame.getCurrentPlayer();

        assertEquals(aiPlayer.getUuid(), playerService.getAiPlayer().getUuid());
        assertEquals(aiPlayer.getName(), playerService.getAiPlayer().getName());
        assertTrue(aiPlayer.isAI());
    }

    @Test
    public void findById_nominal() {
        assertEquals(game, gameService.findById(game.getUuid()));
    }

    @Test
    public void findById_badGame() {
        assertNull(gameService.findById(UUID.randomUUID()));
    }

    @Test
    public void findPlayerById_nominal() {
        Player player = game.getCurrentPlayer();
        assertEquals(player, gameService.findPlayerById(player.getUuid(), game));
    }

    @Test
    public void findPlayerById_badId() {
        assertThrows(NoSuchElementException.class, () -> gameService.findPlayerById(UUID.randomUUID(), game));
    }

    @Test
    public void endGame_nominal() throws WebSocketException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.getFirst().getUuid();
        UUID winnerId = players.get(1).getUuid();

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinners().getFirst().getUuid());
    }

    @Test
    public void endGame_twice() throws WebSocketException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        UUID winnerId = players.get(1).getUuid();

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinners().getFirst().getUuid());

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinners().getFirst().getUuid());
    }

    @Test
    public void endGame_badGame() throws WebSocketException {
        gameService.endGame(UUID.randomUUID(), UUID.randomUUID());
        assertNull(game.getWinners());
    }

    @Test
    public void endGame_badPlayer() throws UnknownPlayerException, WebSocketException, CardSetException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.getFirst().getUuid();
        game = gameService.createGame(loserId, players.get(1).getUuid());

        gameService.endGame(UUID.randomUUID(), game.getUuid());
        assertNull(game.getWinners());
    }
}
