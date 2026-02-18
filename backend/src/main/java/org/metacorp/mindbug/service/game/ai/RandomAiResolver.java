package org.metacorp.mindbug.service.game.ai;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiPlayerTurnAction;
import org.metacorp.mindbug.model.ai.TurnActionType;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.AiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomAiResolver implements AiResolver {

    protected static final Random RND = new Random();

    @Override
    public boolean shouldMindbug(Game game, AiPlayer player) {
        return RND.nextBoolean();
    }

    @Override
    public CardInstance chooseBlocker(List<CardInstance> availableBlockers, Game game) {
        int randomValue = RND.nextInt(availableBlockers.size() + 1);
        return randomValue == availableBlockers.size() ? null : availableBlockers.get(randomValue);
    }

    @Override
    public EffectsToApply chooseSimultaneousEffect(Game game) {
        List<EffectsToApply> shuffledEffects = new ArrayList<>(((SimultaneousEffectsChoice) game.getChoice()).getEffectsToSort());
        Collections.shuffle(shuffledEffects);

        return shuffledEffects.getFirst();
    }

    @Override
    public List<CardInstance> chooseTargets(Game game) {
        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        List<CardInstance> shuffledCards = new ArrayList<>(targetChoice.getAvailableTargets());
        Collections.shuffle(shuffledCards);

        // Retrieve a sub list only if there are more available targets than the targets count (can happen due to 'optional' parameter)
        if (shuffledCards.size() > targetChoice.getTargetsCount()) {
            shuffledCards = shuffledCards.subList(0, targetChoice.getTargetsCount());
        }

        return shuffledCards;
    }

    @Override
    public CardInstance chooseHunterTarget(Game game) {
        List<CardInstance> shuffledCards = new ArrayList<>(((HunterChoice) game.getChoice()).getAvailableTargets());
        shuffledCards.add(null); // Add null value as we may not want to use HUNTER
        Collections.shuffle(shuffledCards);

        return shuffledCards.getFirst();
    }

    @Override
    public boolean shouldAttackAgain(Game game) {
        return RND.nextBoolean();
    }

    @Override
    public boolean resolveBooleanChoice(Game game) {
        return RND.nextBoolean();
    }

    @Override
    public AiPlayerTurnAction getTurnAction(List<CardInstance> availableAttackers, List<CardInstance> availableActionCards, Game game) {
        List<TurnActionType> availableGameActions = new ArrayList<>();
        Player currentPlayer = game.getCurrentPlayer();

        if (!currentPlayer.getHand().isEmpty()) {
            availableGameActions.add(TurnActionType.PLAY);
        }

        if (!availableAttackers.isEmpty()) {
            availableGameActions.add(TurnActionType.ATTACK);
        }

        if (!availableActionCards.isEmpty()) {
            availableGameActions.add(TurnActionType.ACTION);
        }

        AiPlayerTurnAction turnAction = new AiPlayerTurnAction();
        turnAction.setType(availableGameActions.get(RND.nextInt(availableGameActions.size())));

        switch (turnAction.getType()) {
            case PLAY -> turnAction.setTarget(AiUtils.getRandomCard(currentPlayer.getHand()));
            case ACTION -> turnAction.setTarget(AiUtils.getRandomCard(availableActionCards));
            case ATTACK -> turnAction.setTarget(AiUtils.getRandomCard(availableAttackers));
        }

        return turnAction;
    }
}
