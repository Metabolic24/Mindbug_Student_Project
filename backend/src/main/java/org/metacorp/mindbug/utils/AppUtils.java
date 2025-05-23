package org.metacorp.mindbug.utils;

import lombok.Setter;
import org.metacorp.mindbug.app.GameEngine;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayCardService;
import org.metacorp.mindbug.service.StartService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public final class AppUtils {

    private static final Random RND = new Random();

    @Setter
    private static boolean verbose = false;

    public static Game startGame() {
        Game game = StartService.newGame(new Player("Player1"), new Player("Player2"));

        System.out.println("\nDEBUT DU JEU !!!\n");
        nextTurn(game);

        return game;
    }

    public static void play(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();

        if (currentPlayer.getHand().isEmpty()) {
            System.out.println("Illegal move : cannot play cards if none in hand");
            return;
        }

        int index = RND.nextInt(currentPlayer.getHand().size());
        CardInstance card = currentPlayer.getHand().get(index);

        System.out.printf("%s joue la carte '%s'\n", currentPlayer.getName(), card.getCard().getName());
        PlayCardService.pickCard(card, game);
        PlayCardService.playCard(game);

        if (game.getChoice() == null && !game.isFinished()) {
            nextTurn(game);
        }
    }

    public static void attack(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();

        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
        if (availableCards.isEmpty()) {
            System.out.println("Illegal move : cannot attack while no card on the board");
            return;
        }

        int index = RND.nextInt(availableCards.size());
        CardInstance attackCard = availableCards.get(index);

        System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), attackCard.getCard().getName());
        AttackService.declareAttack(attackCard, game);

        if (game.isFinished()) {
            return;
        }

        while (game.getChoice() != null && !game.isFinished()) {
            AppUtils.resolveChoice(game);
        }

        if (game.isFinished()) { //TODO Maybe raise an exception to manage finished games
            return;
        }

        // Only resolve attack if there is still an attacking card
        if (game.getAttackingCard() != null) {
            resolveAttack(game);
        }
    }

    public static void frenzyAttack(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        CardInstance attackCard = game.getAttackingCard();

        System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), attackCard.getCard().getName());

        resolveAttack(game);
    }

    private static void resolveAttack(Game game) throws GameStateException {
        Player opponentPlayer = game.getOpponent();

        Stream<CardInstance> blockersStream = opponentPlayer.getBoard().stream().filter(CardInstance::isAbleToBlock);
        if (game.getAttackingCard().hasKeyword(CardKeyword.SNEAKY)) {
            blockersStream = blockersStream.filter((card) -> card.hasKeyword(CardKeyword.SNEAKY));
        }

        List<CardInstance> blockers = blockersStream.toList();
        if (blockers.isEmpty()) {
            System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
            AttackService.resolveAttack(null, game);
        } else {
            int index = RND.nextInt(blockers.size());
            CardInstance defendCard = blockers.get(index);

            System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), defendCard.getCard().getName());
            AttackService.resolveAttack(defendCard, game);
        }

        if (game.getChoice() == null && !game.isFinished()) {
            nextTurn(game);
        }
    }

    public static void resolveChoice(Game game) throws GameStateException {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            System.err.println("Action invalide");
        } else {
            switch (choice.getType()) {
                case SIMULTANEOUS -> {
                    System.out.println("Résolution d'un choix d'ordonnancement d'effets simultanés");

                    SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) choice;
                    List<EffectsToApply> shuffledEffects = new ArrayList<>(simultaneousEffectsChoice.getEffectsToSort());
                    Collections.shuffle(shuffledEffects);

                    System.out.printf("Ordre choisi : %s\n", shuffledEffects.stream().map(effectToApply -> effectToApply.getCard().getCard().getName()).toList());

                    GameService.resolveChoice(shuffledEffects.getFirst().getCard().getUuid(), game);
                }
                case TARGET -> {
                    System.out.println("Résolution d'un choix de cible(s)");
                    TargetChoice targetChoice = (TargetChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(targetChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    shuffledCards = shuffledCards.subList(0, targetChoice.getTargetsCount());
                    System.out.printf("Cible(s) choisie(s) : %s\n", shuffledCards.stream().map(cardInstance -> cardInstance.getCard().getName()).toList());

                    GameService.resolveChoice(shuffledCards.stream().map(CardInstance::getUuid).toList(), game);
                }
                case HUNTER -> {
                    System.out.println("Résolution d'un choix de cible d'attaque");
                    HunterChoice hunterChoice = (HunterChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(hunterChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    System.out.printf("Cible choisie : %s\n", shuffledCards.getFirst().getCard().getName());

                    GameService.resolveChoice(shuffledCards.getFirst().getUuid(), game);
                }
                case FRENZY, BOOLEAN -> {
                    System.out.printf("Résolution d'un choix booléen de type %s\n", choice.getType());

                    boolean randomBoolean = RND.nextBoolean();
                    System.out.printf("Valeur choisie : %s\n", randomBoolean);

                    GameService.resolveChoice(randomBoolean, game);
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

    public static void runAndCheckErrors(Game game, GameEngine engine) {
        try {
            engine.run();
        } catch (GameStateException gst) {
            System.err.println(gst.getMessage());
            throw new RuntimeException(gst);
        } catch (Throwable t) {
            System.out.println("=================================");
            System.out.println("Une erreur s'est produite (cf contexte ci-dessous)");

            for (Player player : game.getPlayers()) {
                AppUtils.detailedSumUpPlayer(player);
                System.out.println("=================================");
            }

            System.out.println(game);

            throw t;
        }
    }
}
