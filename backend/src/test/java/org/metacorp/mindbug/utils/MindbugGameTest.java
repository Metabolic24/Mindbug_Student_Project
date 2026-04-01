package org.metacorp.mindbug.utils;

import ch.qos.logback.classic.LoggerContext;
import jakarta.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiLevel;
import org.metacorp.mindbug.model.card.CardSetName;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.CardSetService;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class that allows to clean log files only if the test succeeded
 */
public class MindbugGameTest {

    private static final List<UUID> gameIds = new ArrayList<>();
    private static UUID gameId;

    protected static ServiceLocator locator;

    @Inject
    protected PlayerService playerService;

    @Inject
    protected StartService startService;

    @Inject
    protected GameService gameService;

    @Inject
    protected CardSetService cardSetService;

    @RegisterExtension
    private final AfterTestExecutionCallback afterTest = context -> {
        final Optional<Throwable> exception = context.getExecutionException();
        if (exception.isEmpty()) {
            gameIds.add(gameId);
        }
    };

    protected Game startGame(Player player1, Player player2) throws CardSetException {
        return startGame(player1, player2, CardSetName.FIRST_CONTACT);
    }

    protected Game startGame(Player player1, Player player2, CardSetName cardSetName) throws CardSetException {
        Game game = startService.startGame(new Game(Arrays.asList(player1, player2)), cardSetName);
        gameId = game.getUuid();
        return game;
    }

    protected Game startGame2v2(Player player1, Player player2,Player player3, Player player4) throws CardSetException {
        return startGame2v2(player1, player2,player3, player4, CardSetName.FIRST_CONTACT);
    }

    protected Game startGame2v2(Player player1, Player player2,Player player3, Player player4, CardSetName cardSetName) throws CardSetException {
        Game game = startService.startGame(new Game(Arrays.asList(player1, player2, player3, player4)), cardSetName);
        gameId = game.getUuid();
        return game;
    }

    protected Game prepareCustomGame() throws CardSetException {
        Game game = TestGameUtils.prepareCustomGame(playerService, cardSetService);
        gameId = game.getUuid();
        return game;
    }

    protected Game createGame(UUID player1, UUID player2) throws UnknownPlayerException, CardSetException {
        Game game = player2 == null ?
                gameService.createOfflineGame(player1, AiLevel.RANDOM, CardSetName.FIRST_CONTACT) :
                gameService.createGame(player1, player2);

        gameId = game.getUuid();
        return game;
    }

    protected Game getAppUtilsGame() throws CardSetException {
        Game game = AppUtils.startGame(playerService, startService, true, false);
        gameId = game.getUuid();
        return game;
    }

    @BeforeAll
    public static void initLocator() {
        locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
    }

    @BeforeEach
    public void injectServices() {
        locator.inject(this);
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
