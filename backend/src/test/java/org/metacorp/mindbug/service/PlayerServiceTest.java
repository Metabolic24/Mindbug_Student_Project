package org.metacorp.mindbug.service;

import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.UnknownPlayerException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    private final PlayerService playerService = new PlayerService();

    @Test
    public void createPlayer_nominal() {
        String name = "player1";
        PlayerLightDTO player = playerService.createPlayer(name);

        assertNotNull(player);
        assertEquals(name, player.getName());
        assertNotNull(player.getUuid());

        name = "player2";
        PlayerLightDTO player2 = playerService.createPlayer(name);

        assertNotNull(player2);
        assertEquals(name, player2.getName());
        assertNotNull(player2.getUuid());
        assertNotEquals(player.getUuid(), player2.getUuid());
    }

    @Test
    public void getPlayer_nominal() throws UnknownPlayerException {
        String name = "player1";
        PlayerLightDTO player = playerService.createPlayer(name);

        assertEquals(player, playerService.getPlayer(player.getUuid()));
    }

    @Test
    public void getPlayer_unknownPlayer() {
        assertThrows(UnknownPlayerException.class, () -> playerService.getPlayer(UUID.randomUUID()));
    }
}
