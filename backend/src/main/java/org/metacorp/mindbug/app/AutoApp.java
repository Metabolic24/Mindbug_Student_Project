package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Mindbug application that launches a game then uses random choices to reach the end of it
 */
public class AutoApp {

    private static final Random random = new Random();

    public static void main(String[] args) {
        Game game = AppUtils.startGame();

        AppUtils.runAndCheckErrors(game, () -> {
            do {
                resolveTurn(game);
            } while (!game.isFinished());
        });
    }

    /**
     * Resolve a game turn
     *
     * @param game the current game
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static void resolveTurn(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();

        boolean attack = currentPlayer.getHand().isEmpty() || (!availableCards.isEmpty() && random.nextBoolean());
        if (attack) {
            // Declare attack
            AppUtils.declareAttack(game);
            resolveChoices(game);

            // Resolve attack or Frenzy case
            while (game.getAttackingCard() != null && !game.isFinished()) {
                AppUtils.resolveAttack(game);
                resolveChoices(game);
            }
        } else {
            // Play a card
            AppUtils.play(game);
            resolveChoices(game);
        }

        if (!game.isFinished()) {
            AppUtils.nextTurn(game);
        }
    }

    /**
     * Resolve zero, one or multiple choices
     *
     * @param game the current game
     */
    private static void resolveChoices(Game game) throws GameStateException {
        while (game.getChoice() != null && !game.isFinished()) {
            resolveChoice(game);
        }
    }

    /**
     * Resolve the current choice
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    private static void resolveChoice(Game game) throws GameStateException {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            System.err.println("Action invalide");
        } else {
            switch (choice.getType()) {
                case SIMULTANEOUS -> {
                    System.out.println("\nRésolution d'un choix d'ordonnancement d'effets simultanés");

                    SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) choice;
                    List<EffectsToApply> shuffledEffects = new ArrayList<>(simultaneousEffectsChoice.getEffectsToSort());
                    Collections.shuffle(shuffledEffects);

                    System.out.printf("Ordre choisi : %s\n", shuffledEffects.stream().map(effectToApply -> effectToApply.getCard().getCard().getName()).toList());

                    ChoiceService.resolveChoice(shuffledEffects.getFirst().getCard().getUuid(), game);
                }
                case TARGET -> {
                    System.out.println("\nRésolution d'un choix de cible(s)");
                    TargetChoice targetChoice = (TargetChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(targetChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    // Retrieve a sub list only if there are more available targets than the targets count (can happen due to 'optional' parameter)
                    if (shuffledCards.size() > targetChoice.getTargetsCount()) {
                        shuffledCards = shuffledCards.subList(0, targetChoice.getTargetsCount());
                    }

                    System.out.printf("Cible(s) choisie(s) : %s\n", shuffledCards.stream().map(cardInstance -> cardInstance.getCard().getName()).toList());

                    ChoiceService.resolveChoice(shuffledCards.stream().map(CardInstance::getUuid).toList(), game);
                }
                case HUNTER -> {
                    System.out.println("\nRésolution d'un choix de cible d'attaque");
                    HunterChoice hunterChoice = (HunterChoice) choice;

                    List<CardInstance> shuffledCards = new ArrayList<>(hunterChoice.getAvailableTargets());
                    Collections.shuffle(shuffledCards);

                    System.out.printf("Cible choisie : %s\n", shuffledCards.getFirst().getCard().getName());

                    ChoiceService.resolveChoice(shuffledCards.getFirst().getUuid(), game);
                }
                case FRENZY, BOOLEAN -> {
                    System.out.printf("\nRésolution d'un choix booléen de type %s\n", choice.getType());

                    boolean randomBoolean = AppUtils.nextBoolean();
                    System.out.printf("Valeur choisie : %s\n", randomBoolean);

                    ChoiceService.resolveChoice(randomBoolean, game);
                }
            }
        }
    }
}
