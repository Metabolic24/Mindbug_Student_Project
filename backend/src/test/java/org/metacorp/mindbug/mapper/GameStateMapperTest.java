package org.metacorp.mindbug.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateMapperTest {

    private final GameService gameService = new GameService();
    private Game game;

    @BeforeEach
    public void setUp() throws UnknownPlayerException {
        Player player1 = new Player(PlayerService.createPlayer("player1"));
        Player player2 = new Player(PlayerService.createPlayer("player2"));
        game = gameService.createGame(player1.getUuid(), player2.getUuid());
    }

    @Test
    public void fromGame_startedGame() {
        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
    }

    @Test
    public void fromGame_playedCard() {

    }

    private void comparePlayers(Player player, PlayerDTO playerDTO) {
        assertEquals(player.getUuid(), playerDTO.getUuid());
        assertEquals(player.getName(), playerDTO.getName());
        assertEquals(player.getMindBugs(), playerDTO.getMindbugCount());
        assertEquals(player.getDrawPile().size(), playerDTO.getDrawPileCount());
        assertEquals(player.getTeam().getLifePoints(), playerDTO.getLifePoints());
        assertEquals(player.getDisabledTiming(), playerDTO.getDisabledTiming());

        compareCards(player.getHand(), playerDTO.getHand());
        compareCards(player.getBoard(), playerDTO.getBoard());
        compareCards(player.getDiscardPile(), playerDTO.getDiscard());
    }

    private void compareCards(List<CardInstance> playerHand, List<CardDTO> playerDTOHand) {
        assertEquals(playerHand.size(), playerDTOHand.size());

        for (CardInstance playerCard : playerHand) {
            boolean found = false;

            for (CardDTO playerDTOCard : playerDTOHand) {
                if (playerCard.getUuid().equals(playerDTOCard.getUuid())) {
                    assertEquals(playerCard.getCard().getName(), playerDTOCard.getName());
                    assertEquals(playerCard.getPower(), playerDTOCard.getPower());
                    assertEquals(playerCard.getCard().getId(), playerDTOCard.getId());
                    assertEquals(playerCard.getKeywords(), playerDTOCard.getKeywords());
                    assertEquals(playerCard.getCard().getSetName(), playerDTOCard.getSetName());
                    assertEquals(playerCard.getOwner().getUuid(), playerDTOCard.getOwnerId());

                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
    }
}
