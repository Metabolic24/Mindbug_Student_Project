package org.metacorp.mindbug.service.game;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartServiceTest {

    @Test
    public void testStart_nominal() {
        Game game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));

        assertNotNull(game.getCurrentPlayer());
        assertEquals(2, game.getPlayers().size());

        for (Player player : game.getPlayers()) {
            assertNotNull(player);
            assertTrue(player.getName().equals("Player1") || player.getName().equals("Player2"));
            assertNotNull(player.getTeam());
            assertEquals(3, player.getTeam().getLifePoints());
            assertEquals(2, player.getMindBugs());

            assertEquals(5, player.getHand().size());
            assertEquals(5, player.getDrawPile().size());
            assertTrue(player.getDiscardPile().isEmpty());
            assertTrue(player.getBoard().isEmpty());

            assertTrue(player.getDisabledTiming().isEmpty());
        }

        assertTrue(game.getBannedCards().size() >= 2 && game.getBannedCards().size() % 2 == 0);
        assertEquals(52, game.getCards().size() + game.getBannedCards().size());
    }

    @Disabled("Currently, there are non-implemented stuff that blocks this test execution. Should be enabled when all Beyond Evolution mechanics are available.")
    @Test
    public void testStart_evolutionCards() {
        Game game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")), CardSetName.BEYOND_EVOLUTION);

        assertNotNull(game.getCurrentPlayer());
        assertEquals(2, game.getPlayers().size());

        for (Player player : game.getPlayers()) {
            assertNotNull(player);
            assertTrue(player.getName().equals("Player1") || player.getName().equals("Player2"));
            assertNotNull(player.getTeam());
            assertEquals(3, player.getTeam().getLifePoints());
            assertEquals(2, player.getMindBugs());

            assertEquals(5, player.getHand().size());
            assertEquals(5, player.getDrawPile().size());
            assertTrue(player.getDiscardPile().isEmpty());
            assertTrue(player.getBoard().isEmpty());

            assertTrue(player.getDisabledTiming().isEmpty());
        }

        assertTrue(game.getBannedCards().size() >= 2 && game.getBannedCards().size() % 2 == 0);
        assertEquals(52, game.getCards().size() + game.getBannedCards().size());
        assertEquals(52, game.getEvolutionCards().size());
    }
}
