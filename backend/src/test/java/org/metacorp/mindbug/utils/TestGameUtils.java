package org.metacorp.mindbug.utils;

import lombok.Getter;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.service.game.PlayCardService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestGameUtils {

    private Game game;

    @Getter
    private  Player player1;
    @Getter
    private  Player player2;

    public  Game prepareCustomGame() {
        PlayerService playerService = new PlayerService();
        player1 = new Player(playerService.createPlayer("player1"));
        player2 = new Player(playerService.createPlayer("player2"));

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

    public  CardInstance getCardById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).findFirst().orElse(null);
    }

    public  List<CardInstance> getCardsById(int id) {
        return game.getCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == id).collect(Collectors.toList());
    }

    public  void attack(CardInstance attackingCard, CardInstance defendingCard) throws GameStateException {
        AttackService.declareAttack(attackingCard, game);
        AttackService.resolveAttack(defendingCard, game);
    }

    public  void play(CardInstance pickedCard) throws GameStateException {
        play(pickedCard, null);
    }

    public  void play(CardInstance pickedCard, Player mindbugger) throws GameStateException {
        PlayCardService.pickCard(pickedCard, game);
        if (game.getChoice() != null && game.getChoice().getType() == ChoiceType.BOOLEAN) {
            // Auto-resolve mindbug choice in tests: use mindbug only if explicitly provided.
            ChoiceService.resolveChoice(mindbugger != null, game);
        }
        if (game.getChoice() == null && game.getPlayedCard() != null) {
            PlayCardService.playCard(mindbugger, game);
        }
    }
   
    public void hand(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getHand().add(card);
        }
    }

    public void board(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getBoard().add(card);
        }
    }

    public  void discard(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getDiscardPile().add(card);
        }
    }

    public void draw(Player player, CardInstance... cards) {
        for (CardInstance card : cards) {
            card.setOwner(player);
            player.getDrawPile().add(card);
        }
    }

    public void huntTarget(CardInstance card) throws GameStateException {
        ChoiceService.resolveChoice(card == null ? null : card.getUuid(), game);
    }

    public  void chooseTargets(CardInstance... cards) throws GameStateException {
        ChoiceService.resolveChoice(Arrays.stream(cards).map(CardInstance::getUuid).toList(), game);
    }

    public  void choose(boolean choice) throws GameStateException {
        ChoiceService.resolveChoice(choice, game);
    }

    public TestGameUtils() {
        // Not to be used
    }
}
