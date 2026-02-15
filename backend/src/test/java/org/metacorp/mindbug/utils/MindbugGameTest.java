package org.metacorp.mindbug.utils;

import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class that allows to clean log files only if the test succeeded
 */
public class MindbugGameTest {

    private static final List<UUID> gameIds = new ArrayList<>();
    private static UUID gameId;

    @RegisterExtension
    private final AfterTestExecutionCallback afterTest = context -> {
        final Optional<Throwable> exception = context.getExecutionException();
        if (exception.isEmpty()) {
            gameIds.add(gameId);
        }
    };

    protected Game startGame(Player player1, Player player2) {
        Game game = StartService.startGame(player1, player2);
        gameId = game.getUuid();
        return game;
    }

    protected Game startGame(Player player1, Player player2, CardSetName cardSetName) {
        Game game = StartService.startGame(new Game(player1, player2), cardSetName);
        gameId = game.getUuid();
        return game;
    }

    protected Game prepareCustomGame() {
        Game game = TestGameUtils.prepareCustomGame();
        gameId = game.getUuid();
        return game;
    }

    protected Game createGame(GameService gameService, UUID player1, UUID player2) throws UnknownPlayerException {
        Game game = gameService.createGame(player1, player2);
        gameId = game.getUuid();
        return game;
    }

    protected Game getAppUtilsGame() {
        Game game = AppUtils.startGame(new PlayerService());
        gameId = game.getUuid();
        return game;
    }

    @AfterAll
    public static void tearDown() throws IOException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        for (UUID gameId : gameIds) {
            Files.deleteIfExists(Path.of(System.getProperty("user.dir"), "test-log", gameId.toString() + ".log"));
        }
    }
}
