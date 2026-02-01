package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.ProtectionEffect;
import org.metacorp.mindbug.model.modifier.ProtectionModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProtectionEffectResolverTest {

    private Game game;
    private CardInstance effectSource;
    private Player currentPlayer;

    private ProtectionEffect effect;
    private ProtectionEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        effectSource = currentPlayer.getHand().getFirst();

        currentPlayer.addCardToBoard(effectSource);
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        effect = new ProtectionEffect();
        effect.setType(EffectType.PROTECTION);
        effectResolver = new ProtectionEffectResolver(effect);
        timing = EffectTiming.ATTACK;
    }

    @Test
    public void testBasic_selfOnAttack() {
        effect.setSelf(true);
        effect.setAllies(false);

        effectResolver.apply(game, effectSource, timing);

        for (CardInstance card : currentPlayer.getBoard()) {
            if (card.equals(effectSource)) {
                assertTrue(card.hasProtection());
                assertEquals(1, card.getModifiers().size());
                assertInstanceOf(ProtectionModifier.class, effectSource.getModifiers().iterator().next());
            } else {
                assertFalse(card.hasProtection());
                assertTrue(card.getModifiers().isEmpty());
            }
        }
    }

    @Test
    public void testBasic_allies() {
        effect.setSelf(false);
        effect.setAllies(true);
        timing = EffectTiming.PASSIVE;

        effectResolver.apply(game, effectSource, timing);

        for (CardInstance card : currentPlayer.getBoard()) {
            if (!card.equals(effectSource)) {
                assertTrue(card.hasProtection());
            } else {
                assertFalse(card.hasProtection());
            }

            assertTrue(card.getModifiers().isEmpty());
        }
    }
}
