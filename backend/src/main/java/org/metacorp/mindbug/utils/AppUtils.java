package org.metacorp.mindbug.utils;

import lombok.Setter;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.choice.simultaneous.SimultaneousEffectsChoice;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class AppUtils {

    private static Random random;
    @Setter
    private static boolean verbose = false;

    public static Game startGame() {
        Game game = new Game("Player1", "Player2");
        random = new Random();

        System.out.println("\nDEBUT DU JEU !!!\n");
        nextTurn(game);

        return game;
    }

    public static void play(Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        if (currentPlayer.getHand().isEmpty()) {
            System.out.println("Illegal move : cannot play cards if none in hand");
            return;
        }

        int index = random.nextInt(currentPlayer.getHand().size());
        CardInstance card = currentPlayer.getHand().get(index);

        System.out.printf("%s joue la carte '%s'\n", currentPlayer.getName(), card.getCard().getName());
        game.pickCard(card);
        game.playCard(false);

        if (game.getChoice() == null && !game.isFinished()) {
            nextTurn(game);
        }
    }

    public static void attack(Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isCanAttack).toList();
        if (availableCards.isEmpty()) {
            System.out.println("Illegal move : cannot attack while no card on the board");
            return;
        }

        int index = random.nextInt(availableCards.size());
        CardInstance attackCard = availableCards.get(index);

        System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), attackCard.getCard().getName());
        game.declareAttack(attackCard);

        if (game.isFinished()) {
            return;
        }

        while (game.getChoice() != null && !game.isFinished()) {
            AppUtils.resolveChoice(game);
        }

        if (game.isFinished()) { //TODO Maybe raise an exception to manage finished games
            return;
        }

        resolveAttack(game);
    }

    public static void frenzyAttack(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance attackCard = game.getAttackingCard();

        System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), attackCard.getCard().getName());

        resolveAttack(game);
    }

    private static void resolveAttack(Game game) {
        Player opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());

        Stream<CardInstance> blockersStream = opponentPlayer.getBoard().stream().filter(CardInstance::isCanBlock);
        if (game.getAttackingCard().hasKeyword(Keyword.SNEAKY)) {
            blockersStream = blockersStream.filter((card) -> card.hasKeyword(Keyword.SNEAKY));
        }

        List<CardInstance> blockers = blockersStream.toList();
        if (blockers.isEmpty()) {
            System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
            game.resolveAttack(null);
        } else {
            int index = random.nextInt(blockers.size());
            CardInstance defendCard = blockers.get(index);

            System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), defendCard.getCard().getName());
            game.resolveAttack(defendCard);
        }

        if (game.getChoice() == null && !game.isFinished()) {
            nextTurn(game);
        }
    }

    public static void resolveChoice(Game game) {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            System.err.println("Action invalide");
        } else {
            switch (choice.getType()) {
                case SIMULTANEOUS -> {
                    System.out.println("Résolution d'un choix d'ordonnancement d'effets simultanés");

                    SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) choice;
                    List<EffectToApply> shuffledEffects = new ArrayList<>(simultaneousEffectsChoice.getEffectsToSort());
                    Collections.shuffle(shuffledEffects);

                    System.out.printf("Ordre choisi : %s\n", shuffledEffects.stream().map(effectToApply -> effectToApply.getCard().getCard().getName()).toList());

                    game.resolveChoice(shuffledEffects.stream().map(EffectToApply::getUuid).toList());
                }
                case TARGET -> {
                    System.out.println("Résolution d'un choix de cible(s)");
                    TargetChoice targetChoice = (TargetChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(targetChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    shuffledCards = shuffledCards.subList(0, targetChoice.getTargetsCount());
                    System.out.printf("Cible(s) choisie(s) : %s\n", shuffledCards.stream().map(cardInstance -> cardInstance.getCard().getName()).toList());

                    game.resolveChoice(shuffledCards.stream().map(CardInstance::getUuid).toList());
                }
                case FRENZY, BOOLEAN -> {
                    System.out.printf("Résolution d'un choix booléen de type %s\n", choice.getType());

                    boolean randomBoolean = random.nextBoolean();
                    System.out.printf("Valeur choisie : %s\n", randomBoolean);

                    game.resolveChoice(randomBoolean);
                }
            }
        }

        if (game.getChoice() == null) {
            nextTurn(game);
        }
    }

    public static void detailedSumUpPlayer(Player player) {
        System.out.printf("%s : %d PV, %d Mindbug(s), %d carte(s) restante(s)\n", player.getName(), player.getTeam().getLifePoints(), player.getMindBugs(), player.getDrawPile().size());
        displayCards(player.getHand(), "Main");
        displayCards(player.getBoard(), "Terrain");
        displayCards(player.getDiscardPile(), "Défausse");
    }

    public static void displayCards(List<CardInstance> cards, String location) {
        if (!cards.isEmpty()) {
            System.out.printf("\t%s : %d cartes\n", location, cards.size());
            for (CardInstance card : cards) {
                System.out.printf("\t- %s (%d) %s \n", card.getCard().getName(), card.getPower(),
                        card.getKeywords().isEmpty() ? "" : card.getKeywords().toString());
            }
        }
    }

    public static void nextTurn(Game game) {
        for (Player player : game.getPlayers()) {
            if (verbose) {
                detailedSumUpPlayer(player);
                System.out.println();
            }
        }

        System.out.printf("Au tour de %s\n", game.getCurrentPlayer().getName());
    }

    public static void runAndCheckErrors(Game game, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            t.printStackTrace();

            for (Player player: game.getPlayers()) {
                AppUtils.detailedSumUpPlayer(player);
                System.out.println("=================================");
            }

            System.out.println(game);
        }
    }
}
