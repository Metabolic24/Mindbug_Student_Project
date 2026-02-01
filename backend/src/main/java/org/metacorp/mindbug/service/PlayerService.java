package org.metacorp.mindbug.service;

import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.UnknownPlayerException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service that manages players
 */
@Service
public class PlayerService {
    /**
     * The map that stores players
     */
    private final Map<UUID, PlayerLightDTO> players = new HashMap<>();

    /**
     * Create a new player
     *
     * @param playerName the player name
     * @return the created player as a PlayerLightDTO
     */
    public PlayerLightDTO createPlayer(String playerName) {
        UUID randomUuid;
        do {
            randomUuid = UUID.randomUUID();
        } while (players.containsKey(randomUuid));

        PlayerLightDTO player = new PlayerLightDTO();
        player.setName(playerName);
        player.setUuid(randomUuid);

        players.put(randomUuid, player);

        return player;
    }

    /**
     * Get player data by ID
     *
     * @param playerId the player ID
     * @return the player data if any
     * @throws UnknownPlayerException if no player has been found
     */
    public PlayerLightDTO getPlayer(UUID playerId) throws UnknownPlayerException {
        if (players.containsKey(playerId)) {
            return players.get(playerId);
        } else {
            throw new UnknownPlayerException(playerId);
        }
    }
}
