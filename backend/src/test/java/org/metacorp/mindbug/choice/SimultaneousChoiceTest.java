package org.metacorp.mindbug.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.effect.GainEffect;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.StartService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimultaneousChoiceTest {
    private Game game;
    private Player currentPlayer;
    private Player opponent;

    @BeforeEach
    public void initGame() {
        game = StartService.newGame("Player1", "Player2");
        currentPlayer = game.getCurrentPlayer();
        opponent = currentPlayer.getOpponent(game.getPlayers());
    }

    @Test
    public void testResolveSimultaneousChoice() {
        GainEffect attackEffect = new GainEffect();
        CardInstance attackCard = currentPlayer.getHand().removeFirst();
        attackCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(attackEffect));
        currentPlayer.getDiscardPile().add(attackCard);

        GainEffect defendEffect = new GainEffect();
        CardInstance defendingCard = opponent.getHand().removeFirst();
        defendingCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(defendEffect));
        opponent.getDiscardPile().add(defendingCard);

        SimultaneousEffectsChoice choice = new SimultaneousEffectsChoice(new HashSet<>(Arrays.asList(
                new EffectsToApply(Collections.singletonList(attackEffect), attackCard),
                new EffectsToApply(Collections.singletonList(defendEffect), defendingCard)
        )));
        game.setChoice(choice);

        choice.resolve(choice.getEffectsToSort().stream().map(EffectsToApply::getUuid).toList(), game);

        assertNull(game.getChoice());
        assertEquals(2, game.getEffectQueue().size());

        for (EffectsToApply effect : game.getEffectQueue()) {
            assertTrue(effect.getCard().equals(attackCard) || effect.getCard().equals(defendingCard));
        }
    }
}
