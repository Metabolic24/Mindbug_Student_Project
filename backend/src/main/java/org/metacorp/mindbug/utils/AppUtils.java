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

        if (game.getChoice() == null) {
            nextTurn(game);
        }
    }

    public static void attack(Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        if (currentPlayer.getBoard().isEmpty()) {
            System.out.println("Illegal move : cannot attack while no card on the board");
            return;
        }

        int index = random.nextInt(currentPlayer.getBoard().size());
        CardInstance attackCard = currentPlayer.getBoard().get(index);

        System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), attackCard.getCard().getName());
        game.declareAttack(currentPlayer.getBoard().getFirst());

        Player opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
        List<CardInstance> blockers = new ArrayList<>(opponentPlayer.getBoard());
        if (attackCard.hasKeyword(Keyword.SNEAKY)) {
            blockers = blockers.stream().filter((card) -> card.hasKeyword(Keyword.SNEAKY)).toList();
        }

        if (blockers.isEmpty()) {
            System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
            game.resolveAttack(null);
        } else {
            index = random.nextInt(blockers.size());
            CardInstance defendCard = blockers.get(index);

            System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), defendCard.getCard().getName());
            game.resolveAttack(defendCard);
        }

        if (game.getChoice() == null) {
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

                    System.out.printf("Ordre choisi : %s\n", shuffledEffects);

                    game.resolveChoice(shuffledEffects.stream().map(EffectToApply::getUuid).toList());
                }
                case TARGET -> {
                    System.out.println("Résolution d'un choix de cibles");
                    TargetChoice targetChoice = (TargetChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(targetChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    shuffledCards = shuffledCards.subList(0, targetChoice.getTargetsCount());
                    System.out.printf("Cibles choisies : %s\n", shuffledCards);

                    game.resolveChoice(shuffledCards.stream().map(CardInstance::getUuid).toList());
                }
                case FRENZY, BOOLEAN -> {
                    System.out.println("Résolution d'un choix booléen");

                    game.resolveChoice(random.nextBoolean());
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
            }
        }
    }
}
