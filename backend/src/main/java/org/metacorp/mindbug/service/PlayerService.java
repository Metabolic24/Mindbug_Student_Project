package org.metacorp.mindbug.service;

import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.UnknownPlayerException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service that manages players
 */
public class PlayerService {
    /**
     * The map that stores players
     */
    private static final Map<UUID, PlayerLightDTO> PLAYERS = new HashMap<>();

    /**
     * Create a new player
     *
     * @param playerName the player name
     * @return the created player as a PlayerLightDTO
     */
    public static PlayerLightDTO createPlayer(String playerName) {
        UUID randomUuid;
        do {
            randomUuid = UUID.randomUUID();
        } while (PLAYERS.containsKey(randomUuid));

        PlayerLightDTO player = new PlayerLightDTO();
        player.setName(playerName);
        player.setUuid(randomUuid);

        PLAYERS.put(randomUuid, player);

        return player;
    }

    /**
     * Get player data by ID
     *
     * @param playerId the player ID
     * @return the player data if any
     * @throws UnknownPlayerException if no player has been found
     */
    public static PlayerLightDTO getPlayer(UUID playerId) throws UnknownPlayerException {
        if (PLAYERS.containsKey(playerId)) {
            return PLAYERS.get(playerId);
        } else {
            throw new UnknownPlayerException(playerId);
        }
    }
}
