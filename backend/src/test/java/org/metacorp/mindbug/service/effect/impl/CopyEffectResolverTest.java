package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.CopyEffect;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CopyEffectResolverTest {
    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private CopyEffect effect;
    private CopyEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        timing = EffectTiming.PLAY;

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.getEffects(timing).clear();
        randomCard.getEffects(EffectTiming.PASSIVE).clear();
        currentPlayer.addCardToBoard(randomCard);

        effect = new CopyEffect();
        effect.setType(EffectType.COPY);
        effectResolver = new CopyEffectResolver(effect, randomCard);
    }

    @Test
    public void testWithTiming_nominal() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();

        // Avoid modifying the same base card than random card one
        if (otherCard.getCard().equals(randomCard.getCard())) {
            otherCard = opponentPlayer.getHand().get(1);
        }

        List<Effect> playEffects = otherCard.getEffects(timing);
        playEffects.clear();

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(2);
        playEffects.add(gainEffect);

        otherCard.getCard().getEffects().put(timing, playEffects);

        opponentPlayer.addCardToBoard(otherCard);

        effect.setTiming(timing);
        effectResolver.apply(game, timing);

        assertEquals(1, game.getEffectQueue().size());

        EffectsToApply effectsToApply = game.getEffectQueue().poll();
        assertNotNull(effectsToApply);
        assertEquals(timing, effectsToApply.getTiming());
        assertTrue(effectsToApply.getEffects().contains(gainEffect));
        assertEquals(randomCard, effectsToApply.getCard());
        assertNull(game.getChoice());
    }

    @Test
    public void testWithTiming_noEffect() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        otherCard.getEffects(timing).clear();
        opponentPlayer.addCardToBoard(otherCard);

        effect.setTiming(timing);
        effectResolver.apply(game, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNull(game.getChoice());
    }

    @Test
    public void testWithTiming_choice() {
        CardInstance otherCard = opponentPlayer.getHand().getFirst();
        List<Effect> playEffects = otherCard.getEffects(timing);
        playEffects.clear();

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(2);
        playEffects.add(gainEffect);

        otherCard.getCard().getEffects().put(timing, playEffects);
        opponentPlayer.addCardToBoard(otherCard);

        CardInstance otherCard2 = currentPlayer.getHand().getFirst();
        playEffects = otherCard2.getEffects(timing);
        playEffects.clear();

        InflictEffect inflictEffect = new InflictEffect();
        inflictEffect.setValue(1);
        playEffects.add(inflictEffect);

        otherCard2.getCard().getEffects().put(timing, playEffects);
        currentPlayer.addCardToBoard(otherCard2);

        effect.setTiming(timing);
        effectResolver.apply(game, timing);

        assertTrue(game.getEffectQueue().isEmpty());
        assertNotNull(game.getChoice());

        TargetChoice choice = assertInstanceOf(TargetChoice.class, game.getChoice());
        assertEquals(1, choice.getTargetsCount());
        assertEquals(currentPlayer, choice.getPlayerToChoose());
        assertEquals(effectResolver, choice.getEffect());
        assertTrue(choice.getAvailableTargets().contains(otherCard));
        assertTrue(choice.getAvailableTargets().contains(otherCard2));
        assertEquals(randomCard, choice.getEffectSource());
    }
}

