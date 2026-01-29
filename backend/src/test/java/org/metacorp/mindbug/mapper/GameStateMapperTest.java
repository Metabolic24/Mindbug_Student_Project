package org.metacorp.mindbug.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.choice.TargetChoiceDTO;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.effect.impl.DiscardEffectResolver;
import org.metacorp.mindbug.service.game.PlayCardService;

import java.util.ArrayList;
import java.util.HashSet;
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
        comparePlayers(game.getOpponent().get(0), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_playedCard() throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance playedCard = currentPlayer.getHand().getFirst();
        PlayCardService.pickCard(playedCard, game);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent().get(0), gameStateDTO.getOpponent());

        assertNotNull(gameStateDTO.getCard());
        compareCard(playedCard, gameStateDTO.getCard());

        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_endedGame() {
        gameService.endGame(game.getCurrentPlayer().getUuid(), game.getUuid());

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent().get(0), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertEquals(game.getOpponent().get(0).getUuid(), gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_forcedAttack() {
        game.setForcedAttack(true);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent().get(0), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertTrue(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_targetChoice() {
        game.setChoice(new TargetChoice(game.getCurrentPlayer(), game.getCurrentPlayer().getHand().getFirst(), new DiscardEffectResolver(new DiscardEffect()), 1, new HashSet<>(game.getOpponent().get(0).getHand())));

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent().get(0), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());

        TargetChoiceDTO choiceDTO = assertInstanceOf(TargetChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.TARGET, choiceDTO.getType());
        compareCards(game.getOpponent().get(0).getHand(), new ArrayList<>(choiceDTO.getAvailableTargets()));
        assertEquals(1, choiceDTO.getTargetsCount());
        assertFalse(choiceDTO.getOptional());
        compareCard(game.getCurrentPlayer().getHand().getFirst(), choiceDTO.getSourceCard());
        assertEquals(game.getCurrentPlayer().getUuid(), choiceDTO.getPlayerToChoose());

        assertFalse(gameStateDTO.isForcedAttack());
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
                    compareCard(playerCard,  playerDTOCard);

                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
    }

    private void compareCard(CardInstance card, CardDTO cardDTO) {
        assertEquals(card.getCard().getName(), cardDTO.getName());
        assertEquals(card.getPower(), cardDTO.getPower());
        assertEquals(card.getCard().getId(), cardDTO.getId());
        assertEquals(card.getKeywords(), cardDTO.getKeywords());
        assertEquals(card.getCard().getSetName(), cardDTO.getSetName());
        assertEquals(card.getOwner().getUuid(), cardDTO.getOwnerId());
        assertEquals(card.isStillTough(), cardDTO.isStillTough());
        assertEquals(card.isAbleToBlock(), cardDTO.isAbleToBlock());
        assertEquals(card.isAbleToAttack(), cardDTO.isAbleToAttack());
        assertEquals(card.isAbleToAttackTwice(), cardDTO.isAbleToAttackTwice());
        assertEquals(!card.getEffects(EffectTiming.ACTION).isEmpty(), cardDTO.isHasAction());
    }
}
