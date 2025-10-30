package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private Game game;
    private Player currentPlayer;
    private GameService gameService;

    @BeforeEach
    public void initGame() {
        gameService = new GameService();
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
    }

    @Test
    public void createGame_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        game = gameService.createGame(players.get(0).getUuid(), players.get(1).getUuid());

        assertNotNull(game);
        assertNotNull(game.getUuid());
    }

    @Test
    public void findById_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        game = gameService.createGame(players.get(0).getUuid(), players.get(1).getUuid());

        assertEquals(game, gameService.findById(game.getUuid()));
    }

    @Test
    public void findById_badGame() {
        assertNull(gameService.findById(UUID.randomUUID()));
    }

    @Test
    public void endGame_nominal() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        UUID winnerId = players.get(1).getUuid();
        game = gameService.createGame(loserId, winnerId);

        gameService.endGame(loserId, game.getUuid());
        assertEquals(winnerId, game.getWinner().getUuid());
    }

    @Test
    public void endGame_badGame() {
        gameService.endGame(UUID.randomUUID(), UUID.randomUUID());
        assertNull(game.getWinner());
    }

    @Test
    public void endGame_badPlayer() throws UnknownPlayerException {
        List<Player> players = game.getPlayers();
        UUID loserId = players.get(0).getUuid();
        game = gameService.createGame(loserId, players.get(1).getUuid());

        gameService.endGame(UUID.randomUUID(), game.getUuid());
        assertNull(game.getWinner());
    }

    @Test
    public void testLifePointsLost_endGame() {
        currentPlayer.getTeam().setLifePoints(0);
        GameService.lifePointLost(currentPlayer, game);

        assertTrue(game.isFinished());
    }

    @Test
    public void testLifePointsLost_effect() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(boardCard);
        boardCard.getCard().getEffects().put(EffectTiming.LIFE_LOST, new ArrayList<>(List.of(new InflictEffect())));

        GameService.lifePointLost(currentPlayer, game);
        assertFalse(game.isFinished());

        assertEquals(1, game.getEffectQueue().size());
        assertEquals(boardCard, game.getEffectQueue().getFirst().getCard());
        assertEquals(1, game.getEffectQueue().getFirst().getEffects().size());
        assertInstanceOf(InflictEffect.class, game.getEffectQueue().getFirst().getEffects().getFirst());
    }

    @Test
    public void defeatCard_tough() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(boardCard);
        boardCard.getKeywords().add(CardKeyword.TOUGH);
        boardCard.setStillTough(true);

        GameService.defeatCard(boardCard, game.getEffectQueue());
        assertFalse(boardCard.isStillTough());
        assertTrue(currentPlayer.getBoard().contains(boardCard));
        assertFalse(currentPlayer.getDiscardPile().contains(boardCard));
        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void defeatCard_noMoreTough() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        currentPlayer.addCardToBoard(boardCard);
        boardCard.getKeywords().add(CardKeyword.TOUGH);
        boardCard.setStillTough(false);
        boardCard.getCard().getEffects().put(EffectTiming.DEFEATED, new ArrayList<>(List.of(new GainEffect())));

        GameService.defeatCard(boardCard, game.getEffectQueue());
        assertFalse(boardCard.isStillTough());
        assertFalse(currentPlayer.getBoard().contains(boardCard));
        assertTrue(currentPlayer.getDiscardPile().contains(boardCard));

        assertEquals(1, game.getEffectQueue().size());
        assertEquals(boardCard, game.getEffectQueue().getFirst().getCard());
        assertEquals(1, game.getEffectQueue().getFirst().getEffects().size());
        assertInstanceOf(GainEffect.class, game.getEffectQueue().getFirst().getEffects().getFirst());
    }

    @Test
    public void refreshGameState_noPassiveEffects() {
        CardInstance boardCard = currentPlayer.getHand().getFirst();
        boardCard.setPower(boardCard.getPower() + 1);
        boardCard.setAbleToAttack(false);
        boardCard.setAbleToBlock(false);
        boardCard.setKeywords(new HashSet<>());
        boardCard.getCard().getEffects().put(EffectTiming.PASSIVE, new ArrayList<>());

        currentPlayer.addCardToBoard(boardCard);

        Player opponent = currentPlayer.getOpponent(game.getPlayers());
        opponent.getDiscardPile().add(opponent.getHand().removeFirst());
        opponent.getDiscardPile().add(opponent.getHand().removeFirst());
        opponent.getDiscardPile().add(opponent.getDrawPile().removeFirst());
        opponent.getDiscardPile().add(opponent.getDrawPile().removeFirst());
        opponent.getDiscardPile().add(opponent.getDrawPile().removeFirst());
        opponent.getDiscardPile().add(opponent.getDrawPile().removeFirst());

        GameService.refreshGameState(game);

        boardCard = currentPlayer.getBoard().getFirst();
        assertEquals(boardCard.getCard().getPower(), boardCard.getPower());
        assertEquals(boardCard.getCard().getKeywords(), boardCard.getKeywords());
        assertTrue(boardCard.isAbleToAttack());
        assertTrue(boardCard.isAbleToBlock());

        assertEquals(5, currentPlayer.getHand().size());
        assertEquals(4, currentPlayer.getDrawPile().size());

        assertEquals(4, opponent.getHand().size());
        assertEquals(0, opponent.getDrawPile().size());
    }

    @Test
    public void refreshGameState_multiplePassiveEffects() {
        // Create a new game manually, as we need to get some specific cards
        Player currentPlayer = new Player(PlayerService.createPlayer("player1"));
        Player opponent =  new Player(PlayerService.createPlayer("player2"));
        game = new Game(currentPlayer, opponent);
        game.setCurrentPlayer(currentPlayer);
        game.setCards(CardUtils.getCardsFromConfig(CardSetName.FIRST_CONTACT.getKey()));

        CardInstance card1 = findCard("Instigateur Gobelouille");
        CardInstance card2 = findCard("Requin Crabe Chien Momie Pieuvre");
        CardInstance card3 = findCard("Dr Axolotl");
        CardInstance card4 = findCard("Crapaud Bombe");
        CardInstance card5 = findCard("Pachypoulpe");
        CardInstance card6 = findCard("Scarabouclier");
        CardInstance card7 = findCard("Oursins Hurleurs");
        CardInstance card8 = findCard("Lanceur d'escargots");

        // Put cards into players' board
        card2.setOwner(currentPlayer);
        currentPlayer.getBoard().add(card2);
        card5.setOwner(currentPlayer);
        currentPlayer.getBoard().add(card5);
        card7.setOwner(currentPlayer);
        currentPlayer.getBoard().add(card7);

        card1.setOwner(opponent);
        opponent.getBoard().add(card1);
        card3.setOwner(opponent);
        opponent.getBoard().add(card3);
        card4.setOwner(opponent);
        opponent.getBoard().add(card4);
        card6.setOwner(opponent);
        opponent.getBoard().add(card6);
        card8.setOwner(opponent);
        opponent.getBoard().add(card8);

        GameService.refreshGameState(game);

        // Card1
        assertEquals(10, card1.getPower());
        assertEquals(1, card1.getKeywords().size());
        assertTrue(card1.hasKeyword(CardKeyword.HUNTER));
        assertTrue(card1.isAbleToBlock());

        // Card2
        assertEquals(7, card2.getPower());
        assertEquals(3, card2.getKeywords().size());
        assertTrue(card2.hasKeyword(CardKeyword.HUNTER));
        assertTrue(card2.hasKeyword(CardKeyword.POISONOUS));
        assertTrue(card2.hasKeyword(CardKeyword.FRENZY));

        // Card3
        assertEquals(5, card3.getPower());
        assertEquals(1, card3.getKeywords().size());
        assertTrue(card3.hasKeyword(CardKeyword.POISONOUS));
        assertTrue(card3.isAbleToBlock());

        // Card4
        assertEquals(6, card4.getPower());
        assertEquals(1, card4.getKeywords().size());
        assertTrue(card4.hasKeyword(CardKeyword.FRENZY));
        assertTrue(card4.isAbleToBlock());

        // Card5
        assertEquals(9, card5.getPower());
        assertEquals(1, card5.getKeywords().size());
        assertTrue(card5.hasKeyword(CardKeyword.TOUGH));

        // Card6
        assertEquals(4, card6.getPower());
        assertEquals(3, card6.getKeywords().size());
        assertTrue(card6.hasKeyword(CardKeyword.TOUGH));
        assertTrue(card6.hasKeyword(CardKeyword.HUNTER));
        assertTrue(card6.hasKeyword(CardKeyword.POISONOUS));

        // Card7
        assertEquals(5, card7.getPower());
        assertEquals(1, card7.getKeywords().size());
        assertTrue(card7.hasKeyword(CardKeyword.HUNTER));
    }

    private CardInstance findCard(String cardName) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getName().equals(cardName)).findFirst().orElseThrow();
    }
}
