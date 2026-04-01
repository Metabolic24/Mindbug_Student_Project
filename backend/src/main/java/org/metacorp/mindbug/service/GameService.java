package org.metacorp.mindbug.service;

import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiLevel;
import org.metacorp.mindbug.model.card.CardSetName;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.GameStateService;
import org.metacorp.mindbug.service.game.StartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service to store and manage games
 */
@Service
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    /**
     * The map used to store active games
     */
    private final Map<UUID, Game> games = new HashMap<>();

    @Inject
    private PlayerService playerService;

    @Inject
    private StartService startService;

    /**
     * Create a new game using FIRST_CONTACT set
     *
     * @param player1Id the first player ID
     * @param player2Id the second player ID
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     * @throws CardSetException if an error occurs while retrieving card set
     */
    public Game createGame(UUID player1Id, UUID player2Id) throws UnknownPlayerException, CardSetException {
        return createGame(new HashSet<>(Arrays.asList(player1Id, player2Id)), CardSetName.FIRST_CONTACT);
    }

    /**
     * Create a new game
     *
     * @param playerIds the player IDs
     * @param setName   the card set to be used for this game
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     * @throws CardSetException if an error occurs while retrieving card set
     */
    public Game createGame(Set<UUID> playerIds, CardSetName setName) throws UnknownPlayerException, CardSetException {
        List<Player> players = new ArrayList<>();
        for (UUID playerId : playerIds) {
            players.add(new Player(playerService.getPlayer(playerId)));
        }

        return createGame(players, setName);
    }

    /**
     * Create a new game
     *
     * @param player1Id the first player ID
     * @param aiLevel the AI level
     * @param setName   the card set to be used for this game
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     * @throws CardSetException if an error occurs while retrieving card set
     */
    public Game createOfflineGame(UUID player1Id, AiLevel aiLevel, CardSetName setName) throws UnknownPlayerException, CardSetException {
        List<Player> players = new ArrayList<>();
        players.add(new Player(playerService.getPlayer(player1Id)));
        players.add(new AiPlayer(playerService.getAiPlayer(), aiLevel));

        return createGame(players, setName);
    }

    /**
     * Create a new game
     *
     * @param players the players list
     * @param setName   the card set to be used for this game
     * @return the created Game
     * @throws CardSetException if an error occurs while retrieving card set
     */
    private Game createGame(List<Player> players, CardSetName setName) throws CardSetException {
        Game game = new Game(players);
        games.put(game.getUuid(), game);

        LOGGER.debug("Game created : {}", game.getUuid());

        startService.startGame(game, setName);

        return game;
    }

    /**
     * Get a game by ID
     *
     * @param gameId the game ID
     * @return the corresponding game if any, null otherwise
     */
    public Game findById(UUID gameId) {
        return games.get(gameId);
    }

    /**
     * Get a player by ID
     *
     * @param playerId the player ID
     * @param game     the game where the player should be found
     * @return the corresponding player in the given game if any, null otherwise
     */
    public static Player findPlayerById(UUID playerId, Game game) {
        return game.getPlayers().stream().filter(player -> player.getUuid().equals(playerId)).findFirst().orElseThrow();
    }

    /**
     * Ends a currently active game
     *
     * @param losingPlayerID the losing player ID
     * @param gameId         the current game ID
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public void endGame(UUID losingPlayerID, UUID gameId) throws WebSocketException {
        Game game = findById(gameId);
        if (game != null && !game.isFinished()) {
            Optional<Player> losingPlayer = game.getPlayers().stream()
                    .filter(player -> player.getUuid().equals(losingPlayerID))
                    .findFirst();
            if (losingPlayer.isPresent()) {
                GameStateService.endGame(losingPlayer.get(), game);
            } else {
                game.getLogger().warn("Unable to find losing player {} in game {}...", losingPlayerID, gameId);
            }
        }
    }
}
