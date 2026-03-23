package org.metacorp.mindbug.utils;

import lombok.Getter;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardSetName;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.CardSetService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.service.game.PlayCardService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestGameUtils {

    private static Game game;

    @Getter
    private static Player player1;
    @Getter
    private static Player player2;
    @Getter
    private static Player player3;
    @Getter
    private static Player player4;

    public static Game prepareCustomGame(PlayerService playerService, CardSetService cardSetService) throws CardSetException {
        player1 = new Player(playerService.createPlayer("player1"));
        player2 = new Player(playerService.createPlayer("player2"));

        return prepareCustomGame(List.of(player1, player2), cardSetService);
    }

    public static Game prepareCustomGame2v2(PlayerService playerService, CardSetService cardSetService) throws CardSetException {
        player1 = new Player(playerService.createPlayer("player1"));
        player2 = new Player(playerService.createPlayer("player2"));
        player3 = new Player(playerService.createPlayer("player3"));
        player4 = new Player(playerService.createPlayer("player4"));

        return prepareCustomGame(List.of(player1, player2, player3, player4), cardSetService);
    }

    private static Game prepareCustomGame(List<Player> players, CardSetService cardSetService) throws CardSetException {
        game = new Game(players);

        List<CardInstance> cards = cardSetService.getCardInstances(CardSetName.FIRST_CONTACT.getKey());
        Collections.shuffle(cards);
        game.setCards(cards);

        game.setCurrentPlayer(player1);

        return game;
    }

    public static CardInstance getCardById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).findFirst().orElse(null);
    }

    public static List<CardInstance> getCardsById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).collect(Collectors.toList());
    }

    public static void attack(CardInstance attackingCard, CardInstance defendingCard) throws GameStateException, WebSocketException {
        AttackService.declareAttack(attackingCard, game);
        if (game.getAttackingCard() != null && game.getChoice() == null) {
            AttackService.resolveAttack(defendingCard, game);
        }
    }

    public static void play(CardInstance pickedCard) throws GameStateException, WebSocketException {
        play(pickedCard, null);
    }

    public static void play(CardInstance pickedCard, Player mindbugger) throws GameStateException, WebSocketException {
        PlayCardService.pickCard(pickedCard, game);
        PlayCardService.playCard(mindbugger, game);
    }

    public static void hand(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getHand().add(card);
        }
    }

    public static void board(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getBoard().add(card);
        }
    }

    public static void discard(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getDiscardPile().add(card);
        }
    }

    public static void draw(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getDrawPile().add(card);
        }
    }

    public static void huntTarget(CardInstance card) throws GameStateException, WebSocketException {
        ChoiceService.resolveChoice(card == null ? null : card.getUuid(), game);
    }

    public static void chooseTargets(CardInstance... cards) throws GameStateException, WebSocketException {
        ChoiceService.resolveChoice(Arrays.stream(cards).map(CardInstance::getUuid).toList(), game);
    }

    public static void choose(boolean choice) throws GameStateException, WebSocketException {
        ChoiceService.resolveChoice(choice, game);
    }

    private TestGameUtils() {
        // Not to be used
    }
}
