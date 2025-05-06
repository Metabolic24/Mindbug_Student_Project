package org.metacorp.mindbug.mapper;

import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;
import org.metacorp.mindbug.dto.choice.ChoiceDTO;
import org.metacorp.mindbug.dto.choice.SimultaneousChoiceDTO;
import org.metacorp.mindbug.dto.choice.TargetChoiceDTO;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.*;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;

import java.util.stream.Collectors;

public class GameStateMapper {

    public static GameStateDTO fromGame(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        // Build the GameStateDTO
        GameStateDTO gameStateDTO = new GameStateDTO(game.getUuid(),
                fromPlayer(currentPlayer),
                fromPlayer(game.getOpponent()),
                game.isFinished());

        // Update the card field if needed
        CardInstance playedCard = game.getPlayedCard();
        CardInstance attackingCard = game.getAttackingCard();
        if (playedCard != null) {
            gameStateDTO.setCard(fromCard(playedCard));
        } else if (attackingCard != null) {
            gameStateDTO.setCard(fromCard(attackingCard));
        }

        // Update the choice field if needed
        if (game.getChoice() != null) {
            gameStateDTO.setChoice(fromChoice(game.getChoice()));
        }

        return gameStateDTO;
    }

    private static PlayerDTO fromPlayer(Player player) {
        PlayerDTO result = new PlayerDTO();
        result.setUuid(player.getUuid());
        result.setName(player.getName());
        result.setLifePoints(player.getTeam().getLifePoints());
        result.setMindbugCount(player.getMindBugs());
        result.setDrawPileCount(player.getDrawPile().size());
        result.setHand(player.getHand().stream().map(GameStateMapper::fromCard).toList());
        result.setBoard(player.getBoard().stream().map(GameStateMapper::fromCard).toList());
        result.setDiscard(player.getDiscardPile().stream().map(GameStateMapper::fromCard).toList());
        result.setDisabledTiming(player.getDisabledTiming());

        return result;
    }

    private static CardDTO fromCard(CardInstance card) {
        CardDTO result = new CardDTO();
        result.setUuid(card.getUuid());
        result.setName(card.getCard().getName());
        result.setPower(card.getPower());
        result.setKeywords(card.getKeywords());
        result.setStillTough(card.isStillTough());
        result.setAbleToBlock(card.isAbleToBlock());
        result.setAbleToAttack(card.isAbleToAttack());
        result.setAbleToAttackTwice(card.isAbleToAttackTwice());
        return result;
    }

    private static AbstractChoiceDTO fromChoice(IChoice<?> choice) {
        switch (choice.getType()) {
            case SIMULTANEOUS -> {
                SimultaneousEffectsChoice simultaneousChoice = (SimultaneousEffectsChoice) choice;
                return new SimultaneousChoiceDTO(simultaneousChoice.getEffectsToSort().stream().map(GameStateMapper::fromEffects).collect(Collectors.toSet()));
            }
            case FRENZY -> {
                FrenzyAttackChoice frenzyChoice = (FrenzyAttackChoice) choice;
                return new ChoiceDTO(choice.getType(), frenzyChoice.getAttackingCard().getOwner().getUuid(), frenzyChoice.getAttackingCard().getUuid());
            }
            case TARGET -> {
                TargetChoice targetChoice = (TargetChoice) choice;
                return new TargetChoiceDTO(targetChoice.getPlayerToChoose().getUuid(), targetChoice.getEffectSource().getUuid(),
                        targetChoice.getAvailableTargets().stream().map(GameStateMapper::fromCard).collect(Collectors.toSet()), targetChoice.getTargetsCount());
            }
            case BOOLEAN -> {
                BooleanChoice booleanChoice = (BooleanChoice) choice;
                return new ChoiceDTO(booleanChoice.getType(), booleanChoice.getPlayerToChoose().getUuid(), booleanChoice.getCard().getUuid());
            }
        }

        // Should not happen
        return null;
    }

    private static CardDTO fromEffects(EffectsToApply effectsToSort) {
        return fromCard(effectsToSort.getCard());
    }
}
