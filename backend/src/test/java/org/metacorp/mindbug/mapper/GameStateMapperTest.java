package org.metacorp.mindbug.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.card.CardDTO;
import org.metacorp.mindbug.dto.choice.BooleanChoiceDTO;
import org.metacorp.mindbug.dto.choice.ChoiceDTO;
import org.metacorp.mindbug.dto.choice.HunterChoiceDTO;
import org.metacorp.mindbug.dto.choice.SimultaneousChoiceDTO;
import org.metacorp.mindbug.dto.choice.TargetChoiceDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.impl.DiscardEffectResolver;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameStateMapperTest extends MindbugGameTest {

    private Game game;

    private Player currentPlayer;

    @BeforeEach
    public void setUp() throws UnknownPlayerException, CardSetException {
        Player player1 = new Player(playerService.createPlayer("player1"));
        Player player2 = new Player(playerService.createPlayer("player2"));
        game = createGame(player1.getUuid(), player2.getUuid());

        currentPlayer = game.getCurrentPlayer();
    }

    @Test
    public void fromGame_startedGame() {
        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(currentPlayer, gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_playedCard() throws GameStateException, WebSocketException {
        CardInstance playedCard = currentPlayer.getHand().getFirst();
        PlayCardService.pickCard(playedCard, game);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(currentPlayer, gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNotNull(gameStateDTO.getCard());
        compareCard(playedCard, gameStateDTO.getCard());

        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_attackingCard() throws GameStateException, WebSocketException {
        CardInstance playedCard = currentPlayer.getHand().getFirst();
        playedCard.getCard().getKeywords().clear();
        playedCard.getEffects(EffectTiming.PLAY).clear();
        playedCard.getEffects(EffectTiming.ATTACK).clear();
        playedCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(playedCard, game);
        PlayCardService.playCard(game);

        CardInstance opponentCard = game.getCurrentPlayer().getHand().getFirst();
        opponentCard.getEffects(EffectTiming.PLAY).clear();
        opponentCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(opponentCard, game);
        PlayCardService.playCard(game);

        AttackService.declareAttack(playedCard, game);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNotNull(gameStateDTO.getCard());
        compareCard(playedCard, gameStateDTO.getCard());

        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_endedGame() throws WebSocketException {
        gameService.endGame(currentPlayer.getUuid(), game.getUuid());

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertEquals(game.getOpponent().getUuid(), gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_forcedAttack() {
        game.setForcedAttack(true);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(currentPlayer, gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());
        assertNull(gameStateDTO.getChoice());
        assertTrue(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_targetChoice() {
        CardInstance playerCard = currentPlayer.getHand().getFirst();
        game.setChoice(new TargetChoice(currentPlayer, playerCard, new DiscardEffectResolver(new DiscardEffect(), playerCard), 1, new HashSet<>(game.getOpponent().getHand())));

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(currentPlayer, gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getCard());
        assertNull(gameStateDTO.getWinner());

        TargetChoiceDTO choiceDTO = assertInstanceOf(TargetChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.TARGET, choiceDTO.getType());
        compareCards(game.getOpponent().getHand(), new ArrayList<>(choiceDTO.getAvailableTargets()));
        assertEquals(1, choiceDTO.getTargetsCount());
        assertFalse(choiceDTO.getOptional());
        compareCard(currentPlayer.getHand().getFirst(), choiceDTO.getSourceCard());
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());

        assertFalse(gameStateDTO.isForcedAttack());
    }

    @Test
    public void fromGame_hunterChoice() throws GameStateException, WebSocketException {
        CardInstance playedCard = currentPlayer.getHand().getFirst();
        playedCard.getCard().getKeywords().clear();
        playedCard.getCard().getKeywords().add(CardKeyword.HUNTER);
        playedCard.getEffects(EffectTiming.PLAY).clear();
        playedCard.getEffects(EffectTiming.ATTACK).clear();
        playedCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(playedCard, game);
        PlayCardService.playCard(game);

        CardInstance opponentCard = game.getCurrentPlayer().getHand().getFirst();
        opponentCard.getEffects(EffectTiming.PLAY).clear();
        opponentCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(opponentCard, game);
        PlayCardService.playCard(game);

        AttackService.declareAttack(playedCard, game);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNotNull(gameStateDTO.getCard());
        compareCard(playedCard, gameStateDTO.getCard());

        assertNull(gameStateDTO.getWinner());
        assertFalse(gameStateDTO.isForcedAttack());

        HunterChoiceDTO choiceDTO = assertInstanceOf(HunterChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.HUNTER, choiceDTO.getType());
        compareCards(game.getOpponent().getBoard(), new ArrayList<>(choiceDTO.getAvailableTargets()));
        compareCard(playedCard, choiceDTO.getSourceCard());
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());
    }

    @Test
    public void fromGame_frenzyChoice() throws GameStateException, WebSocketException {
        CardInstance playedCard = currentPlayer.getHand().getFirst();
        playedCard.getCard().getKeywords().clear();
        playedCard.getCard().getKeywords().add(CardKeyword.SNEAKY);
        playedCard.getCard().getKeywords().add(CardKeyword.FRENZY);
        playedCard.setAbleToAttackTwice(true);
        playedCard.getEffects(EffectTiming.PLAY).clear();
        playedCard.getEffects(EffectTiming.ATTACK).clear();
        playedCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(playedCard, game);
        PlayCardService.playCard(game);

        CardInstance opponentCard = game.getCurrentPlayer().getHand().getFirst();
        opponentCard.getCard().getKeywords().clear();
        opponentCard.getEffects(EffectTiming.PLAY).clear();
        opponentCard.getEffects(EffectTiming.PASSIVE).clear();
        PlayCardService.pickCard(opponentCard, game);
        PlayCardService.playCard(game);

        AttackService.declareAttack(playedCard, game);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getWinner());
        assertFalse(gameStateDTO.isForcedAttack());

        ChoiceDTO choiceDTO = assertInstanceOf(ChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.FRENZY, choiceDTO.getType());
        compareCard(playedCard, choiceDTO.getSourceCard());
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());
    }

    @Test
    public void fromGame_simultaneousChoice() {
        CardInstance firstCard = currentPlayer.getHand().getFirst();
        CardInstance secondCard = game.getOpponent().getHand().getFirst();

        Set<EffectsToApply> simultaneousEffects = new HashSet<>();
        simultaneousEffects.add(new EffectsToApply(Collections.singletonList(new GainEffect()), firstCard, EffectTiming.DEFEATED));
        simultaneousEffects.add(new EffectsToApply(Collections.singletonList(new InflictEffect()), secondCard, EffectTiming.DEFEATED));

        game.setChoice(new SimultaneousEffectsChoice(simultaneousEffects, currentPlayer));

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getWinner());
        assertFalse(gameStateDTO.isForcedAttack());

        SimultaneousChoiceDTO choiceDTO = assertInstanceOf(SimultaneousChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.SIMULTANEOUS, choiceDTO.getType());
        compareCards(Arrays.asList(firstCard, secondCard), new ArrayList<>(choiceDTO.getAvailableEffects()));
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());
    }

    @Test
    public void fromGame_booleanChoice() {
        CardInstance firstCard = currentPlayer.getHand().getFirst();

        game.setChoice(new BooleanChoice(currentPlayer, firstCard, (_, _) -> {}));

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getWinner());
        assertFalse(gameStateDTO.isForcedAttack());

        BooleanChoiceDTO choiceDTO = assertInstanceOf(BooleanChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.BOOLEAN, choiceDTO.getType());
        compareCard(firstCard, choiceDTO.getSourceCard());
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());
    }

    @Test
    public void fromGame_booleanChoicev2() {
        CardInstance firstCard = currentPlayer.getHand().getFirst();
        CardInstance sourceCard = game.getOpponent().getHand().getFirst();

        game.setChoice(new BooleanChoice(currentPlayer, sourceCard, (_, _) -> {}, firstCard));

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        assertNotNull(gameStateDTO);
        assertEquals(game.getUuid(), gameStateDTO.getUuid());

        comparePlayers(game.getCurrentPlayer(), gameStateDTO.getPlayer());
        comparePlayers(game.getOpponent(), gameStateDTO.getOpponent());

        assertNull(gameStateDTO.getWinner());
        assertFalse(gameStateDTO.isForcedAttack());

        BooleanChoiceDTO choiceDTO = assertInstanceOf(BooleanChoiceDTO.class, gameStateDTO.getChoice());
        assertEquals(ChoiceType.BOOLEAN, choiceDTO.getType());
        compareCard(sourceCard, choiceDTO.getSourceCard());
        compareCard(firstCard, choiceDTO.getTargetCard());
        assertEquals(currentPlayer.getUuid(), choiceDTO.getPlayerToChoose());
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
                    compareCard(playerCard, playerDTOCard);

                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
    }

    private void compareCard(CardInstance card, CardDTO cardDTO) {
        assertEquals(card.getPower(), cardDTO.getPower());
        assertEquals(card.getCard().getPower(), cardDTO.getBasePower());
        assertEquals(card.getCard().getId(), cardDTO.getId());
        assertEquals(card.getKeywords(), cardDTO.getKeywords());
        assertEquals(card.getOwner().getUuid(), cardDTO.getOwnerId());
        assertEquals(card.isStillTough(), cardDTO.isStillTough());
        assertEquals(card.isAbleToBlock(), cardDTO.isAbleToBlock());
        assertEquals(card.isAbleToAttack(), cardDTO.isAbleToAttack());
        assertEquals(card.isAbleToAttackTwice(), cardDTO.isAbleToAttackTwice());
        assertEquals(!card.getEffects(EffectTiming.ACTION).isEmpty(), cardDTO.isHasAction());
    }
}
