package org.metacorp.mindbug.mapper;

import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;
import org.metacorp.mindbug.dto.choice.BooleanChoiceDTO;
import org.metacorp.mindbug.dto.choice.ChoiceDTO;
import org.metacorp.mindbug.dto.choice.HunterChoiceDTO;
import org.metacorp.mindbug.dto.choice.SimultaneousChoiceDTO;
import org.metacorp.mindbug.dto.choice.TargetChoiceDTO;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.AbstractChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;

import java.util.stream.Collectors;

public class GameStateMapper {

    public static GameStateDTO fromGame(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        // Build the GameStateDTO
        GameStateDTO gameStateDTO = new GameStateDTO(game.getUuid(),
                fromPlayer(currentPlayer),
                fromPlayer(game.getOpponent()));
        gameStateDTO.setForcedAttack(game.isForcedAttack());

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
            gameStateDTO.setChoice(fromChoice(game.getChoice(), game.getCurrentPlayer()));
        }

        // Update the winner field if needed
        if (game.getWinner() != null) {
            gameStateDTO.setWinner(game.getWinner().getUuid());
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
        result.setId(card.getCard().getId());
        result.setSetName(card.getCard().getSetName());
        result.setUuid(card.getUuid());
        result.setOwnerId(card.getOwner().getUuid());
        result.setName(card.getCard().getName());
        result.setPower(card.getPower());
        result.setBasePower(card.getCard().getPower());
        result.setKeywords(card.getKeywords());
        result.setHasAction(!card.getEffects(EffectTiming.ACTION).isEmpty());
        result.setStillTough(card.isStillTough());
        result.setAbleToBlock(card.isAbleToBlock());
        result.setAbleToAttack(card.isAbleToAttack());
        result.setAbleToAttackTwice(card.isAbleToAttackTwice());
        return result;
    }

    private static AbstractChoiceDTO fromChoice(AbstractChoice<?> choice, Player currentPlayer) {
        AbstractChoiceDTO result = null;

        switch (choice.getType()) {
            case SIMULTANEOUS -> {
                SimultaneousEffectsChoice simultaneousChoice = (SimultaneousEffectsChoice) choice;
                result = new SimultaneousChoiceDTO(currentPlayer.getUuid(), simultaneousChoice.getEffectsToSort().stream()
                        .map(GameStateMapper::fromEffects).collect(Collectors.toSet()));
            }
            case FRENZY -> {
                FrenzyAttackChoice frenzyChoice = (FrenzyAttackChoice) choice;
                result = new ChoiceDTO(choice.getType(), frenzyChoice.getAttackingCard().getOwner().getUuid(),
                        fromCard(frenzyChoice.getAttackingCard()));
            }
            case TARGET -> {
                TargetChoice targetChoice = (TargetChoice) choice;
                result = new TargetChoiceDTO(targetChoice.getPlayerToChoose().getUuid(), fromCard(targetChoice.getEffectSource()),
                        targetChoice.getAvailableTargets().stream().map(GameStateMapper::fromCard).collect(Collectors.toSet()),
                        targetChoice.getTargetsCount(), targetChoice.isOptional());
            }
            case BOOLEAN -> {
                BooleanChoice booleanChoice = (BooleanChoice) choice;
                CardDTO sourceCardDTO = fromCard(booleanChoice.getSourceCard());
                CardDTO targetCardDTO = booleanChoice.getCard() == null
                        ? sourceCardDTO : fromCard(booleanChoice.getCard());

                result = new BooleanChoiceDTO(booleanChoice.getPlayerToChoose().getUuid(), sourceCardDTO, targetCardDTO);
            }
            case HUNTER -> {
                HunterChoice hunterChoice = (HunterChoice) choice;
                result = new HunterChoiceDTO(hunterChoice.getAttackingCard().getOwner().getUuid(), fromCard(hunterChoice.getAttackingCard()),
                        hunterChoice.getAvailableTargets().stream().map(GameStateMapper::fromCard).collect(Collectors.toSet()));
            }
            default -> {
                // Should never happen
            }
        }

        return result;
    }

    private static CardDTO fromEffects(EffectsToApply effectsToSort) {
        return fromCard(effectsToSort.getCard());
    }
}
