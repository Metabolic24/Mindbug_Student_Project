package org.metacorp.mindbug.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.effect.impl.ReviveEffectResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActionServiceTest {

    private Game game;
    private Player currentPlayer;
    private CardInstance card;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        card = currentPlayer.getHand().getFirst();
    }

    @Test
    public void testResolveAction_nominal() throws GameStateException {
        GainEffect gainEffect = new GainEffect();
        gainEffect.setType(EffectType.GAIN);
        gainEffect.setValue(2);

        List<Effect> effects = card.getEffects(EffectTiming.ACTION);
        if (effects.isEmpty()) {
            card.getCard().getEffects().put(EffectTiming.ACTION, new ArrayList<>(List.of(gainEffect)));
        } else {
            effects.clear();
            effects.add(gainEffect);
        }

        ActionService.resolveAction(card, game);

        assertEquals(5, currentPlayer.getTeam().getLifePoints());
        assertNotEquals(currentPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testResolveAction_playedCard() {
        game.setPlayedCard(card);
        assertThrows(GameStateException.class, () -> ActionService.resolveAction(card, game));

        assertEquals(currentPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testResolveAction_choicePending() {
        game.setChoice(new BooleanChoice(currentPlayer, card, new ReviveEffectResolver(null)));
        assertThrows(GameStateException.class, () -> ActionService.resolveAction(card, game));

        assertEquals(currentPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testResolveAction_attackingCard() {
        game.setAttackingCard(card);
        assertThrows(GameStateException.class, () -> ActionService.resolveAction(card, game));

        assertEquals(currentPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testResolveAction_noActionEffect() {
        card.getCard().getEffects().remove(EffectTiming.ACTION);
        assertThrows(GameStateException.class, () -> ActionService.resolveAction(card, game));

        assertEquals(currentPlayer, game.getCurrentPlayer());
    }
}
