package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.utils.MindbugGameTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReviveEffectResolverTest extends MindbugGameTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private ReviveEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
        randomCard = opponentPlayer.getHand().removeFirst();
        opponentPlayer.getDiscardPile().add(randomCard);

        GainEffect gainEffect = new GainEffect();
        gainEffect.setValue(2);
        randomCard.getEffects(EffectTiming.PLAY).clear();
        randomCard.getCard().getEffects().put(EffectTiming.PLAY, List.of(gainEffect));

        ReviveEffect effect = new ReviveEffect();
        effect.setType(EffectType.REVIVE);
        effectResolver = new ReviveEffectResolver(effect, randomCard);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testApply_nominal() {
        effectResolver.apply(game, timing);

        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.BOOLEAN, game.getChoice().getType());

        BooleanChoice booleanChoice = (BooleanChoice) game.getChoice();

        assertEquals(effectResolver, booleanChoice.getEffectResolver());
        assertEquals(randomCard, booleanChoice.getSourceCard());
        assertEquals(opponentPlayer, booleanChoice.getPlayerToChoose());
        assertTrue(game.getEffectQueue().isEmpty());
    }

    @Test
    public void testResolve_true() throws GameStateException, WebSocketException {
        effectResolver.apply(game, timing);
        ((BooleanChoice) game.getChoice()).resolve(true, game);

        assertTrue(opponentPlayer.getDiscardPile().isEmpty());
        assertTrue(opponentPlayer.getBoard().contains(randomCard));
        assertEquals(1, game.getEffectQueue().size());
    }

    @Test
    public void testResolve_false() throws GameStateException, WebSocketException {
        effectResolver.apply(game, timing);
        ((BooleanChoice) game.getChoice()).resolve(false, game);

        assertTrue(opponentPlayer.getBoard().isEmpty());
        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));
        assertTrue(game.getEffectQueue().isEmpty());
    }
}
