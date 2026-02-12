package org.metacorp.mindbug.utils;

import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.game.ActionService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.service.game.PlayCardService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class AiUtils {

    private static final Random RND = new Random();

    public static void processGameEvent(UUID playerId, WsGameEvent gameEvent, GameService gameService) {
        Game game = gameService.findById(gameEvent.getState().getUuid());

        try {
            switch (gameEvent.getType()) {
                case NEW_TURN, STATE -> resolveTurn(game);
                case CARD_PICKED -> resolveMindbug(game, playerId);
                case WAITING_ATTACK_RESOLUTION -> resolveAttack(game);
                case CHOICE -> resolveChoice(game);
                default -> {
                    // Nothing to do
                }
            }
        } catch (GameStateException e) {
            // TODO Manage errors
            e.printStackTrace();
        }
    }

    /**
     * Resolve a game turn
     *
     * @param game the current game
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static void resolveTurn(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        List<HistoryKey> availableGameActions = new ArrayList<>();

        if (!currentPlayer.getHand().isEmpty()) {
            availableGameActions.add(HistoryKey.PLAY);
        }

        List<CardInstance> attackCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
        if (!attackCards.isEmpty()) {
            availableGameActions.add(HistoryKey.ATTACK);
        }

        List<CardInstance> actionCards = currentPlayer.getBoard().stream().filter(card -> !card.getEffects(EffectTiming.ACTION).isEmpty()).toList();
        if (!actionCards.isEmpty()) {
            availableGameActions.add(HistoryKey.ATTACK);
        }

        switch (availableGameActions.get(RND.nextInt(availableGameActions.size()))) {
            case PLAY -> {
                CardInstance card = getRandomCard(currentPlayer.getHand());
                PlayCardService.pickCard(card, game);
            }
            case ACTION -> {
                CardInstance card = getRandomCard(actionCards);
                ActionService.resolveAction(card, game);
            }
            case ATTACK -> {
                CardInstance card = getRandomCard(attackCards);
                AttackService.declareAttack(card, game);
            }
            default -> {
                // Should not happen
            }
        }
    }

    private static void resolveMindbug(Game game, UUID playerId) throws GameStateException {
        Player mindbugger = RND.nextBoolean() ? null : game.getPlayers().stream()
                .filter(player -> player.getUuid().equals(playerId)).findFirst().orElse(null);
        PlayCardService.playCard(mindbugger, game);
    }

    /**
     * Resolve an attack in a manual/automatic game
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    private static void resolveAttack(Game game) throws GameStateException {
        List<CardInstance> availableCards = getBlockersList(game);
        if (availableCards.isEmpty()) {
            AttackService.resolveAttack(null, game);
        } else {
            int randomValue = RND.nextInt(availableCards.size() + 1);

            if (randomValue == availableCards.size()) {
                AttackService.resolveAttack(null, game);
            } else {
                AttackService.resolveAttack(availableCards.get(randomValue), game);
            }
        }
    }

    public static List<CardInstance> getBlockersList(Game game) {
        Player attackedPlayer = game.getAttackingCard().getOwner().getOpponent(game.getPlayers()).get(0);

        Stream<CardInstance> blockersStream = attackedPlayer.getBoard().stream().filter(CardInstance::isAbleToBlock);
        if (game.getAttackingCard().hasKeyword(CardKeyword.SNEAKY)) {
            blockersStream = blockersStream.filter((card) -> card.hasKeyword(CardKeyword.SNEAKY));
        }

        return blockersStream.toList();
    }

    /**
     * Resolve the current choice
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void resolveChoice(Game game) throws GameStateException {
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

                    System.out.printf("Ordre choisi : %s\n", shuffledEffects.stream()
                            .map(effectToApply -> effectToApply.getCard().getCard().getName())
                            .toList());

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

                    System.out.printf("Cible(s) choisie(s) : %s\n", shuffledCards.stream()
                            .map(cardInstance -> cardInstance.getCard().getName())
                            .toList());

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

                    boolean randomBoolean = RND.nextBoolean();
                    System.out.printf("Valeur choisie : %s\n", randomBoolean);

                    ChoiceService.resolveChoice(randomBoolean, game);
                }
                default -> {
                    // Should not happen
                }
            }
        }
    }

    /**
     * Return a random card from the given list
     *
     * @param cards the card list
     * @return a random card from the list
     */
    public static CardInstance getRandomCard(List<CardInstance> cards) {
        return cards.get(RND.nextInt(cards.size()));
    }
}
