package org.metacorp.mindbug.utils;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.service.EffectQueueService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class ChoiceUtils {
    private ChoiceUtils() {

    }

    public static void resolveBooleanChoice(Boolean choiceData, BooleanChoice choice, Game game) {
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
        }
    }

    public static void resolveSimultaneousChoice(List<UUID> sortedEffectIds, SimultaneousEffectsChoice choice, Game game) {
        Set<EffectsToApply> effectsToSort = choice.getEffectsToSort();
        if (sortedEffectIds == null || sortedEffectIds.size() != effectsToSort.size()) {
            //TODO Raise an error
            return;
        }

        for (UUID effectId : sortedEffectIds) {
            EffectsToApply foundEffect = effectsToSort.stream()
                    .filter(effectToApply -> effectToApply.getUuid().equals(effectId))
                    .findFirst().orElseThrow();

            game.getEffectQueue().add(foundEffect);
        }

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
}
