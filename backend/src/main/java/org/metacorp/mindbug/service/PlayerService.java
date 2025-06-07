package org.metacorp.mindbug.service;

import org.metacorp.mindbug.dto.player.PlayerLightDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerService {

    private static final Map<UUID, PlayerLightDTO> players = new HashMap<>();

    public static PlayerLightDTO createPlayer(String playerName) {
        UUID randomUuid;
        do {
            randomUuid = UUID.randomUUID();
        } while (players.containsKey(randomUuid));

        PlayerLightDTO player = new PlayerLightDTO();


        player.setName(playerName);
        player.setUuid(UUID.randomUUID());

        players.put(randomUuid, player);

        return player;
    }
}
