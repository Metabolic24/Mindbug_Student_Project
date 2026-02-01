package org.metacorp.mindbug.service;

import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.GameStateService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.HashMap;
import java.util.Map;
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
     * @param player2Id the second player ID
     * @param setName   the card set to be used for this game
     * @return the created Game
     * @throws UnknownPlayerException if at least one player could not be found in the database
     */
    public Game createGame(UUID player1Id, UUID player2Id, CardSetName setName) throws UnknownPlayerException {
        Player player1 = new Player(playerService.getPlayer(player1Id));
        Player player2 = new Player(playerService.getPlayer(player2Id));

        Game game = StartService.newGame(player1, player2, setName);
        games.put(game.getUuid(), game);

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
     */
    public void endGame(UUID losingPlayerID, UUID gameId) {
        Game game = findById(gameId);
        if (game != null) {
            game.getPlayers().stream()
                    .filter(player -> player.getUuid().equals(losingPlayerID))
                    .findFirst().ifPresent(player -> GameStateService.endGame(player, game));
        }
    }
}
