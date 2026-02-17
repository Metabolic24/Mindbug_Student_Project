package org.metacorp.mindbug.service.game.ai;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiPlayerTurnAction;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectsToApply;

import java.util.List;

public interface AiResolver {

    boolean shouldMindbug();

    CardInstance chooseBlocker(List<CardInstance> availableBlockers, Game game);

    EffectsToApply chooseSimultaneousEffect(Game game);

    List<CardInstance> chooseTargets(Game game);

    CardInstance chooseHunterTarget(Game game);

    boolean shouldAttackAgain(Game game);

    boolean resolveBooleanChoice(Game game);

    AiPlayerTurnAction getTurnAction(List<CardInstance> availableAttackers, List<CardInstance> availableActionCards, Game game);
}

