package org.metacorp.mindbug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            }
            case FRENZY -> {
                FrenzyAttackChoice choice = (FrenzyAttackChoice) game.getChoice();
                sourceCard = choice.getAttackingCard();
                data.put("playerToChoose", sourceCard.getOwner().getUuid());
            }
            case TARGET -> {
                TargetChoice choice = (TargetChoice) game.getChoice();
                sourceCard = choice.getEffectSource();
                targets = choice.getAvailableTargets();
                data.put("playerToChoose", choice.getPlayerToChoose().getUuid());
                data.put("targetsCount", choice.getTargetsCount());
            }
            case BOOLEAN -> {
                BooleanChoice choice = (BooleanChoice) game.getChoice();
                sourceCard = choice.getSourceCard();
                targets = Collections.singleton(choice.getCard());
                data.put("playerToChoose", choice.getPlayerToChoose().getUuid());
            }
            case HUNTER -> {
                HunterChoice choice = (HunterChoice) game.getChoice();
                sourceCard = choice.getAttackingCard();
                targets = choice.getAvailableTargets();
                data.put("playerToChoose", sourceCard.getOwner().getUuid());
            }
            default -> {
                // Should never happen
            }
        }

        log(game, HistoryKey.CHOICE, sourceCard, targets, data);
    }

    public static void logEffect(Game game, EffectType type, CardInstance source, Collection<CardInstance> targets) {
        log(game, HistoryKey.EFFECT, source, targets, Map.of("type", type.name()));
    }

    public static void logLifeUpdate(Game game, Player player) {
        log(game, HistoryKey.LIFE, null, null, Map.of("player", player.getUuid(), "life", player.getTeam().getLifePoints()));
    }

    public static void logStart(Game game) {
        Map<String, Object> data = new HashMap<>();
        for (Player player : game.getPlayers()) {
            data.put(player.getUuid().toString(), HistoryMapper.toHistoryCards(player.getHand()));
        }

        log(game, HistoryKey.START, null, null, data);
    }

    public static void saveHistory(Game game) {
        log(game, HistoryKey.END, null, null, Map.of("winner", game.getWinner().getFirst().getUuid()));

        String now = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());

        try {
            Path filePath = Path.of("./log/" + game.getUuid() + "_" + now + ".log");
            Files.createDirectories(filePath.getParent());
            File createdFile = Files.createFile(filePath).toFile();

            try (FileWriter fileWriter = new FileWriter(createdFile)) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, game.getHistory());
            }
        } catch (FileAlreadyExistsException e) {
            // Ignore this error as it may not happen or it is not that important
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Manage errors
        }
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
