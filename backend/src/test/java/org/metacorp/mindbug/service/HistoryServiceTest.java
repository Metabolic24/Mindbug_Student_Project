package org.metacorp.mindbug.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.history.HistoryCard;
import org.metacorp.mindbug.model.history.HistoryEntry;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryServiceTest extends MindbugGameTest {

    private Game game;
    private CardInstance sourceCard;
    private List<CardInstance> targets;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));

        sourceCard = game.getCurrentPlayer().getHand().getFirst();
        targets = Collections.singletonList(game.getOpponent().getHand().getFirst());
    }

    @Test
    public void logStart() {
        assertEquals(1, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().getFirst();
        assertNotNull(historyEntry);
        assertEquals(HistoryKey.START, historyEntry.getKey());
        assertNull(historyEntry.getSource());
        assertTrue(historyEntry.getTargets().isEmpty());
        assertNotNull(historyEntry.getData());

        Object currentPlayerCards = historyEntry.getData().get(game.getCurrentPlayer().getUuid().toString());
        assertNotNull(currentPlayerCards);
        assertEquals(5, ((List<HistoryCard>) currentPlayerCards).size());
    }

    @Test
    public void log_3args() {
        HistoryService.log(game, HistoryKey.PICK, sourceCard);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.PICK, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
    }

    @Test
    public void log_4args() {
        HistoryService.log(game, HistoryKey.PICK, sourceCard, targets);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.PICK, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        compareCard(targets.getFirst(), historyEntry.getTargets().getFirst());
    }

    @Test
    public void logEffect() {
        HistoryService.logEffect(game, EffectType.DESTROY, sourceCard, targets);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.EFFECT, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        compareCard(targets.getFirst(), historyEntry.getTargets().getFirst());
    }

    @Test
    public void logLifeUpdate() {
        HistoryService.logLifeUpdate(game, game.getCurrentPlayer());
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.LIFE, historyEntry.getKey());
        assertNull(historyEntry.getSource());
        assertTrue(historyEntry.getTargets().isEmpty());
        assertNotNull(historyEntry.getData());
        assertEquals(game.getCurrentPlayer().getUuid(), historyEntry.getData().get("player"));
        assertEquals(game.getCurrentPlayer().getTeam().getLifePoints(), historyEntry.getData().get("life"));
    }

    @Test
    public void logChoice_simultaneous() {
        Set<EffectsToApply> effectsToApply = new HashSet<>();
        effectsToApply.add(new EffectsToApply(Collections.singletonList(new GainEffect()), sourceCard, EffectTiming.DEFEATED));
        effectsToApply.add(new EffectsToApply(Collections.singletonList(new GainEffect()), targets.getFirst(), EffectTiming.DEFEATED));

        game.setChoice(new SimultaneousEffectsChoice(effectsToApply, game.getCurrentPlayer()));

        HistoryService.logChoice(game);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.CHOICE, historyEntry.getKey());
        assertNull(historyEntry.getSource());

        int effectsCount = 0;
        for (EffectsToApply effect : effectsToApply) {
            for (int i = 0; i < effectsToApply.size(); i++) {
                if (historyEntry.getTargets().get(i).getUuid().equals(effect.getCard().getUuid())) {
                    compareCard(effect.getCard(), historyEntry.getTargets().get(i));
                    effectsCount++;
                }
            }
        }

        assertEquals(2, effectsCount);

        assertNotNull(historyEntry.getData());
        assertEquals(ChoiceType.SIMULTANEOUS.name(), historyEntry.getData().get("type"));
    }

    @Test
    public void logChoice_hunter() {
        sourceCard.setOwner(game.getOpponent());
        game.setChoice(new HunterChoice(sourceCard, new HashSet<>(targets)));

        HistoryService.logChoice(game);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.CHOICE, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        compareCard(targets.getFirst(), historyEntry.getTargets().getFirst());
        assertNotNull(historyEntry.getData());
        assertEquals(game.getOpponent().getUuid(), historyEntry.getData().get("playerToChoose"));
        assertEquals(ChoiceType.HUNTER.name(), historyEntry.getData().get("type"));
    }

    @Test
    public void logChoice_frenzy() {
        sourceCard.setOwner(game.getOpponent());
        game.setChoice(new FrenzyAttackChoice(sourceCard));

        HistoryService.logChoice(game);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.CHOICE, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        assertTrue(historyEntry.getTargets().isEmpty());
        assertNotNull(historyEntry.getData());
        assertEquals(game.getOpponent().getUuid(), historyEntry.getData().get("playerToChoose"));
        assertEquals(ChoiceType.FRENZY.name(), historyEntry.getData().get("type"));
    }

    @Test
    public void logChoice_boolean() {
        game.setChoice(new BooleanChoice(game.getOpponent(), sourceCard, (_, _) -> {
        }, targets.getFirst()));

        HistoryService.logChoice(game);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.CHOICE, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        compareCard(targets.getFirst(), historyEntry.getTargets().getFirst());
        assertNotNull(historyEntry.getData());
        assertEquals(game.getOpponent().getUuid(), historyEntry.getData().get("playerToChoose"));
        assertEquals(ChoiceType.BOOLEAN.name(), historyEntry.getData().get("type"));
    }

    @Test
    public void logChoice_target() {
        game.setChoice(new TargetChoice(game.getOpponent(), sourceCard, (_, _) -> {
        }, 1, new HashSet<>(targets)));

        HistoryService.logChoice(game);
        assertEquals(2, game.getHistory().size());

        HistoryEntry historyEntry = game.getHistory().get(1);
        assertEquals(HistoryKey.CHOICE, historyEntry.getKey());
        assertNotNull(historyEntry.getSource());
        compareCard(sourceCard, historyEntry.getSource());
        compareCard(targets.getFirst(), historyEntry.getTargets().getFirst());
        assertNotNull(historyEntry.getData());
        assertEquals(game.getOpponent().getUuid(), historyEntry.getData().get("playerToChoose"));
        assertEquals(1, historyEntry.getData().get("targetsCount"));
        assertEquals(ChoiceType.TARGET.name(), historyEntry.getData().get("type"));
    }

    private void compareCard(CardInstance cardInstance, HistoryCard historyCard) {
        assertEquals(cardInstance.getUuid(), historyCard.getUuid());
        assertEquals(cardInstance.getCard().getName(), historyCard.getName());
        assertEquals(cardInstance.getOwner().getUuid(), historyCard.getOwner());
    }
}
