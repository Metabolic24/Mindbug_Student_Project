package org.metacorp.mindbug.service;

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
        Player player1 = new Player(PlayerService.getPlayer(player1Id));
        Player player2 = new Player(PlayerService.getPlayer(player2Id));

        Game game = StartService.newGame(player1, player2, setName);
        games.put(game.getUuid(), game);

        return game;
    }

    /**
     * Crée une nouvelle partie en mode 2v2
     * * @param p1Id ID du premier joueur (Équipe A)
     * @param p2Id ID du deuxième joueur (Équipe B)
     * @param p3Id ID du troisième joueur (Équipe A - coéquipier de p1)
     * @param p4Id ID du quatrième joueur (Équipe B - coéquipier de p2)
     * @param setName le set de cartes à utiliser
     * @return la Game créée avec les PV partagés par équipe
     * @throws UnknownPlayerException si l'un des joueurs est introuvable
     */
    public Game createGame(UUID p1Id, UUID p2Id, UUID p3Id, UUID p4Id, CardSetName setName) throws UnknownPlayerException {
        // 1. Récupération des 4 joueurs depuis la base de données via le PlayerService
        Player p1 = new Player(PlayerService.getPlayer(p1Id));
        Player p2 = new Player(PlayerService.getPlayer(p2Id));
        Player p3 = new Player(PlayerService.getPlayer(p3Id));
        Player p4 = new Player(PlayerService.getPlayer(p4Id));

        // 2. Appel de la méthode StartService.newGame (version 4 joueurs)
        // C'est ici que l'User Story est remplie : StartService va lier les Team instances.
        Game game = StartService.newGame(p1, p2, p3, p4, setName);
        
        // 3. Stockage de la partie en mémoire
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
