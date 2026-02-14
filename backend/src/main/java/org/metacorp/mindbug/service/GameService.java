package org.metacorp.mindbug.service;

import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.GameStateService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to store and manage games
 */
@Service
public class GameService {

    /**
     * The map used to store active games
     */
    private final Map<UUID, Game> games = new HashMap<>();

    @Inject
    private PlayerService playerService;

    /**
     * Create a new game using FIRST_CONTACT set
     *
     * @param player1Id the first player ID
     * @param player2Id the second player ID
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     */
    public Game createGame(UUID player1Id, UUID player2Id) throws UnknownPlayerException {
        return createGame(player1Id, player2Id, CardSetName.FIRST_CONTACT);
    }

    /**
     * Create a new game
     *
     * @param player1Id the first player ID
     * @param player2Id the second player ID (null in case of an offline game)
     * @param setName   the card set to be used for this game
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     */
    public Game createGame(UUID player1Id, UUID player2Id, CardSetName setName) throws UnknownPlayerException {
        Player player1 = new Player(playerService.getPlayer(player1Id));
        Player player2 = player2Id == null ? new AiPlayer(playerService.getAiPlayer()) : new Player(playerService.getPlayer(player2Id));

        Game game = new Game(player1, player2);
        games.put(game.getUuid(), game);

        StartService.startGame(game, setName);

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
