package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimultaneousChoiceTest {
    private Game game;
    private Player currentPlayer;
    private Player opponent;
    private EffectTiming timing;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponent = currentPlayer.getOpponent(game.getPlayers());
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testResolve() throws GameStateException {
        GainEffect attackEffect = new GainEffect();
        CardInstance attackCard = currentPlayer.getHand().removeFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(attackEffect));
        currentPlayer.getDiscardPile().add(attackCard);

        GainEffect defendEffect = new GainEffect();
        CardInstance defendingCard = opponent.getHand().removeFirst();
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(defendEffect));
        opponent.getDiscardPile().add(defendingCard);

        SimultaneousEffectsChoice choice = new SimultaneousEffectsChoice(new HashSet<>(Arrays.asList(
                new EffectsToApply(Collections.singletonList(attackEffect), attackCard, timing),
                new EffectsToApply(Collections.singletonList(defendEffect), defendingCard, timing)
        )));
        game.setChoice(choice);

        choice.resolve(attackCard.getUuid(), game);

        assertNull(game.getChoice());
        assertEquals(2, game.getEffectQueue().size());

        assertEquals(attackCard, game.getEffectQueue().get(0).getCard());
        assertEquals(defendingCard, game.getEffectQueue().get(1).getCard());
    }
}
