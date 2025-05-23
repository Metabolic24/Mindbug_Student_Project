package org.metacorp.mindbug.utils;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.*;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.service.EffectQueueService;
import org.metacorp.mindbug.service.WebSocketService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ChoiceUtils {
    private ChoiceUtils() {

    }

    public static void  resolveBooleanChoice(Boolean choiceData, BooleanChoice choice, Game game) {
        // First reset choice so it doesn't block next steps
        game.setChoice(null);

        if (choice != null) {
            choice.getEffectResolver().resolve(game, choiceData);
        } else {
            //TODO Manage error
        }
    }

    public static void resolveFrenzyChoice(Boolean attackAgain, FrenzyAttackChoice choice, Game game) throws GameStateException {
        // First reset the choice in any case (so it does not block the next steps)
        game.setChoice(null);

        if (attackAgain != null && attackAgain) {
            CardInstance attackingCard = choice.getAttackingCard();

            attackingCard.setAbleToAttackTwice(false);
            AttackService.declareAttack(attackingCard, game);
        } else {
            game.setCurrentPlayer(game.getOpponent());
            WebSocketService.sendGameEvent(WsGameEventType.NEW_TURN, game);
        }
    }

    public static void resolveSimultaneousChoice(UUID cardId, SimultaneousEffectsChoice choice, Game game) {
        if (cardId == null) {
            //TODO Raise an error
            return;
        }

        Set<EffectsToApply> effectsToSort = choice.getEffectsToSort();

        // First add the corresponding effect in the queue
        EffectsToApply foundEffect = effectsToSort.stream()
                .filter(effectToApply -> effectToApply.getCard().getUuid().equals(cardId))
                .findFirst().orElseThrow();
        game.getEffectQueue().add(foundEffect);

        // Then add the other effects
        effectsToSort.remove(foundEffect);
        game.getEffectQueue().addAll(effectsToSort);

        // Reset the choice only if the given choice list was valid
        game.setChoice(null);
    }

    public static void resolveTargetChoice(List<UUID> chosenTargetIds, TargetChoice choice, Game game) {
        if (chosenTargetIds == null || chosenTargetIds.size() != choice.getTargetsCount()) {
            //TODO Raise an error or log message
            return;
        }

        List<CardInstance> chosenTargets = choice.getAvailableTargets().stream().filter(target -> chosenTargetIds.contains(target.getUuid())).toList();
        if (chosenTargets.size() != choice.getTargetsCount()) {
            //TODO Raise an error or log message
        }

        choice.getEffect().resolve(game, chosenTargets);

        // Reset the choice only if the given choice list was valid and if no other choice appeared while resolving current choice
        if (game.getChoice().equals(choice)) {
            game.setChoice(null);
        }
    }

    public static void resolveHunterChoice(UUID chosenTargetId, HunterChoice choice, Game game) throws GameStateException {
        // First reset choice as attack resolution need to have no current choice
        game.setChoice(null);

        if (chosenTargetId != null) {
            Optional<CardInstance> chosenTarget = choice.getAvailableTargets().stream().filter(target -> chosenTargetId.equals(target.getUuid())).findFirst();
            if (chosenTarget.isEmpty()) {
                //TODO Raise an error or log message
            } else {
                AttackService.resolveAttack(chosenTarget.get(), game);
            }
        } else {
            // Only send this event when no target has been selected as game will be refreshed by attack resolution in the other case
            WebSocketService.sendGameEvent(WsGameEventType.ATTACK_DECLARED, game);
        }
    }
}
