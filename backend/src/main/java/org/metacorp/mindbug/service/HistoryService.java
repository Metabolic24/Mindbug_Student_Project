package org.metacorp.mindbug.service;

import org.metacorp.mindbug.mapper.HistoryMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.history.HistoryEntry;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Service to manage history
 */
public class HistoryService {

    /**
     * Add an entry to the game history
     *
     * @param game   the current game
     * @param key    the entry key
     * @param source the source card
     * @see #log(Game, HistoryKey, CardInstance, Collection, Map)
     */
    public static void log(Game game, HistoryKey key, CardInstance source) {
        log(game, key, source, null, null);
    }

    /**
     * Add an entry to the game history
     *
     * @param game    the current game
     * @param key     the entry key
     * @param source  the source card
     * @param targets the target cards
     * @see #log(Game, HistoryKey, CardInstance, Collection, Map)
     */
    public static void log(Game game, HistoryKey key, CardInstance source, Collection<CardInstance> targets) {
        log(game, key, source, targets, null);
    }

    /**
     * Store history data about a game choice
     *
     * @param game the current game state
     */
    public static void logChoice(Game game) {
        CardInstance sourceCard = null;
        Collection<CardInstance> targets = null;
        Map<String, Object> data = new HashMap<>();

        ChoiceType choiceType = game.getChoice().getType();
        data.put("type", choiceType.name());

        switch (choiceType) {
            case SIMULTANEOUS -> {
                SimultaneousEffectsChoice choice = (SimultaneousEffectsChoice) game.getChoice();
                targets = choice.getEffectsToSort().stream().map(EffectsToApply::getCard).toList();
                data.put("playerToChoose", game.getCurrentPlayer().getUuid());

                game.getLogger().info("New {} choice to be resolved by {} (sourceCards : {})", choiceType.name(), getLoggablePlayer(game.getCurrentPlayer()), getLoggableCards(targets));
            }
            case FRENZY -> {
                FrenzyAttackChoice choice = (FrenzyAttackChoice) game.getChoice();
                sourceCard = choice.getAttackingCard();
                data.put("playerToChoose", sourceCard.getOwner().getUuid());

                game.getLogger().info("New {} choice to be resolved by {} (attackingCard : {})", choiceType.name(), getLoggablePlayer(sourceCard.getOwner()), getLoggableCard(sourceCard));
            }
            case TARGET -> {
                TargetChoice choice = (TargetChoice) game.getChoice();
                sourceCard = choice.getEffectSource();
                targets = choice.getAvailableTargets();
                data.put("playerToChoose", choice.getPlayerToChoose().getUuid());
                data.put("targetsCount", choice.getTargetsCount());

                game.getLogger().info("New {} choice to be resolved by {} (targets : {})", choiceType.name(), getLoggablePlayer(choice.getPlayerToChoose()), getLoggableCards(targets));
            }
            case BOOLEAN -> {
                BooleanChoice choice = (BooleanChoice) game.getChoice();
                sourceCard = choice.getSourceCard();

                String targetAsString = "";
                if (choice.getCard() != null) {
                    targets = Collections.singleton(choice.getCard());
                    targetAsString = choice.getCard().getUuid().toString();
                }

                data.put("playerToChoose", choice.getPlayerToChoose().getUuid());

                game.getLogger().info("New {} choice to be resolved by {} (target : {})", choiceType.name(), getLoggablePlayer(choice.getPlayerToChoose()), targetAsString);
            }
            case HUNTER -> {
                HunterChoice choice = (HunterChoice) game.getChoice();
                sourceCard = choice.getAttackingCard();
                targets = choice.getAvailableTargets();
                data.put("playerToChoose", sourceCard.getOwner().getUuid());

                game.getLogger().info("New {} choice to be resolved by {} (attackingCard : {}, targets : {})", choiceType.name(), getLoggablePlayer(sourceCard.getOwner()), getLoggableCard(sourceCard), getLoggableCards(targets));
            }
            default -> {
                // Should never happen
            }
        }

        log(game, HistoryKey.CHOICE, sourceCard, targets, data);

    }

    /**
     * Store history data about a triggered effect
     *
     * @param game    the current game state
     * @param type    the effect type
     * @param source  the effect source
     * @param targets the list of targets (if any)
     */
    public static void logEffect(Game game, EffectType type, CardInstance source, Collection<CardInstance> targets) {
        log(game, HistoryKey.EFFECT, source, targets, Map.of("type", type.name()));
        game.getLogger().info("Resolving {} effect of {}", type.name(), getLoggableCard(source));
    }

    /**
     * Store history data about life points change
     *
     * @param game   the current game state
     * @param player the player whose life points changed
     */
    public static void logLifeUpdate(Game game, Player player) {
        log(game, HistoryKey.LIFE, null, null, Map.of("player", player.getUuid(), "life", player.getTeam().getLifePoints()));
        game.getLogger().info("Life update for player {} : {} LP", getLoggablePlayer(player), player.getTeam().getLifePoints());
    }

    /**
     * Store history data about the initial state of the game
     *
     * @param game the current game state
     */
    public static void logStart(Game game) {
        Logger logger = game.getLogger();
        logger.info("Game {} starting...", game.getUuid());

        Map<String, Object> data = new HashMap<>();
        for (Player player : game.getPlayers()) {
            game.getLogger().info("Player {} hand : {}", getLoggablePlayer(player), getLoggableCards(player.getHand()));
            game.getLogger().info("Player {} draw pile : {}", getLoggablePlayer(player), getLoggableCards(player.getDrawPile()));

            data.put(player.getUuid().toString(), HistoryMapper.toHistoryCards(player.getHand()));
            // TODO Store player draw pile in a dedicated structure instead of a generic map
        }

        log(game, HistoryKey.START, null, null, data);
    }

    /**
     * Save history data into a dedicated log file
     *
     * @param game the current game state
     */
    public static void saveHistory(Game game) {
        log(game, HistoryKey.END, null, null, Map.of("winner", game.getWinner().getUuid()));
        game.getLogger().info("End of the game");
    }

    /**
     * Add an entry to the game history
     *
     * @param game    the current game
     * @param key     the entry key
     * @param source  the source card
     * @param targets the target cards
     * @param data    the auxiliary data
     */
    private static void log(Game game, HistoryKey key, CardInstance source, Collection<CardInstance> targets, Map<String, Object> data) {
        game.getHistory().add(new HistoryEntry(key, HistoryMapper.toHistoryCard(source), HistoryMapper.toHistoryCards(targets), data));
    }
}
