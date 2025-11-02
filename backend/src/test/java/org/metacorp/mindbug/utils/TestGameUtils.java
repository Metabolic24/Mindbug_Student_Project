package org.metacorp.mindbug.utils;

import lombok.Getter;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.service.game.PlayCardService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestGameUtils {

    private static Game game;

    @Getter
    private static Player player1;
    @Getter
    private static Player player2;

    public static Game prepareCustomGame() {
        player1 = new Player(PlayerService.createPlayer("player1"));
        player2 = new Player(PlayerService.createPlayer("player2"));

        game = new Game(player1, player2);

        List<CardInstance> cards = CardUtils.getCardsFromConfig(CardSetName.FIRST_CONTACT.getKey());
        Collections.shuffle(cards);
        game.setCards(cards);

        for (Player player : game.getPlayers()) {
            player.getTeam().setLifePoints(3);
        }

        game.setCurrentPlayer(player1);

        return game;
    }

    public static CardInstance getCardById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).findFirst().orElse(null);
    }

    public static List<CardInstance> getCardsById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).collect(Collectors.toList());
    }

    public static void attack(CardInstance attackingCard, CardInstance defendingCard) throws GameStateException {
        AttackService.declareAttack(attackingCard, game);
        AttackService.resolveAttack(defendingCard, game);
    }

    public static void play(CardInstance pickedCard) throws GameStateException {
        play(pickedCard, null);
    }

    public static void play(CardInstance pickedCard, Player mindbugger) throws GameStateException {
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

    public static void huntTarget(CardInstance card) throws GameStateException {
        ChoiceService.resolveChoice(card == null ? null : card.getUuid(), game);
    }

    public static void chooseTargets(CardInstance... cards) throws GameStateException {
        ChoiceService.resolveChoice(Arrays.stream(cards).map(CardInstance::getUuid).toList(), game);
    }

    public static void choose(boolean choice) throws GameStateException {
        ChoiceService.resolveChoice(choice, game);
    }

    private TestGameUtils() {
        // Not to be used
    }
}
