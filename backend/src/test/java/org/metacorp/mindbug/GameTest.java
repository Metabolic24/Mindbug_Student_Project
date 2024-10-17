package org.metacorp.mindbug;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testStart() {
        Game game = new Game("Player1", "Player2");

        assertNotNull(game.getCurrentPlayer());
        assertEquals(2, game.getPlayers().size());

        for (Player player : game.getPlayers()) {
            assertNotNull(player);
            assertNotNull(player.getName());
            assertNotNull(player.getTeam());
            assertEquals(3, player.getTeam().getLifePoints());
            assertEquals(5, player.getHand().size());
            assertEquals(5, player.getDrawPile().size());
            assertTrue(player.getDiscardPile().isEmpty());
            assertTrue(player.getBoard().isEmpty());
            assertTrue(player.getDisabledTiming().isEmpty());
            assertEquals(2, player.getMindBugs());
        }

        assertTrue(game.getBannedCards().size() >= 2 && game.getBannedCards().size() %2 ==0);
        assertTrue(!game.getCards().isEmpty() && game.getCards().size() <= 50);
        assertEquals(52, game.getCards().size() + game.getBannedCards().size());
    }
}

