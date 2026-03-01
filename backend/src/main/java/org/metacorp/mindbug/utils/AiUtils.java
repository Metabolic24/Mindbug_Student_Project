package org.metacorp.mindbug.utils;

import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiPlayerTurnAction;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.AbstractChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.game.ActionService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.CardService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

public class AiUtils {

    public static void processGameEvent(UUID playerId, WsGameEvent gameEvent, GameService gameService) {
        Game game = gameService.findById(gameEvent.getState().getUuid());
        AiPlayer player = (AiPlayer) gameService.findPlayerById(playerId, game);

        try {
            switch (gameEvent.getType()) {
                case NEW_TURN, STATE -> resolveTurn(game, player);
                case CARD_PICKED -> resolveMindbug(game, player);
                case WAITING_ATTACK_RESOLUTION -> resolveAttack(game, player);
                case CHOICE -> resolveChoice(game, player);
                default -> {
                    // Nothing to do
                }
            }
        } catch (GameStateException | WebSocketException e) {
            game.getLogger().error("Unable to process AI player game event", e);
            // TODO Probably a blocking issue
        }
    }

    /**
     * Resolve a game turn
     *
     * @param game the current game
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static void resolveTurn(Game game, AiPlayer currentPlayer) throws GameStateException, WebSocketException {
        List<CardInstance> attackCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
        List<CardInstance> actionCards = currentPlayer.getBoard().stream().filter(card -> !card.getEffects(EffectTiming.ACTION).isEmpty()).toList();

        AiPlayerTurnAction action = currentPlayer.getResolver().getTurnAction(attackCards, actionCards, game);
        switch (action.getType()) {
            case PLAY -> PlayCardService.pickCard(action.getTarget(), game);
            case ACTION -> ActionService.resolveAction(action.getTarget(), game);
            case ATTACK -> AttackService.declareAttack(action.getTarget(), game);
            default -> {
                // Should not happen
            }
        }
    }

    private static void resolveMindbug(Game game, AiPlayer aiPlayer) throws GameStateException, WebSocketException {
        boolean shouldMindbug = aiPlayer.getResolver().shouldMindbug(game, aiPlayer);
        PlayCardService.playCard(shouldMindbug ? aiPlayer : null, game);
    }

    /**
     * Resolve an attack in a manual/automatic game
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    private static void resolveAttack(Game game, AiPlayer aiPlayer) throws GameStateException, WebSocketException {
        List<CardInstance> availableCards = CardService.getBlockersList(game);
        if (availableCards.isEmpty()) {
            AttackService.resolveAttack(null, game);
        } else {
            CardInstance blockingCard = aiPlayer.getResolver().chooseBlocker(availableCards, game);
            AttackService.resolveAttack(blockingCard, game);
        }
    }

    /**
     * Resolve the current choice
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void resolveChoice(Game game, AiPlayer aiPlayer) throws GameStateException, WebSocketException {
        Logger logger = game.getLogger();

        AbstractChoice<?> choice = game.getChoice();
        if (choice == null) {
            throw new GameStateException("No choice to be resolved by AI player");
        } else {
            logger.debug("Resolving {} choice...", choice.getType().name());

            switch (choice.getType()) {
                case SIMULTANEOUS -> {
                    EffectsToApply chosenEffect = aiPlayer.getResolver().chooseSimultaneousEffect(game);
                    ChoiceService.resolveChoice(chosenEffect.getCard().getUuid(), game);
                }
                case TARGET -> {
                    List<CardInstance> chosenTargets = aiPlayer.getResolver().chooseTargets(game);
                    ChoiceService.resolveChoice(chosenTargets.stream().map(CardInstance::getUuid).toList(), game);
                }
                case HUNTER -> {
                    CardInstance chosenTarget = aiPlayer.getResolver().chooseHunterTarget(game);
                    ChoiceService.resolveChoice(chosenTarget == null ? null : chosenTarget.getUuid(), game);
                }
                case FRENZY -> ChoiceService.resolveChoice(aiPlayer.getResolver().shouldAttackAgain(game), game);
                case BOOLEAN -> ChoiceService.resolveChoice(aiPlayer.getResolver().resolveBooleanChoice(game), game);
                default -> {
                    // Should not happen
                }
            }
        }
    }
}
