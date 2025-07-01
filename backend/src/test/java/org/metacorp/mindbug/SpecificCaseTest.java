package org.metacorp.mindbug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.AttackService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metacorp.mindbug.utils.TestGameUtils.*;

public class SpecificCaseTest {

    private Game game;

    private Player player1;
    private Player player2;

    @BeforeEach
    public void setup() {
        game = prepareCustomGame();
        player1 = getPlayer1();
        player2 = getPlayer2();
    }

    @Test
    public void hurlerAlwaysBoost() throws GameStateException {
        CardInstance turboBug = getCardById(30);
        CardInstance majesticManticore = getCardById(42);
        CardInstance snailHydra = getCardById(25);
        CardInstance ferretPacifier = getCardById(36);
        CardInstance urchinHurler = getCardById(32);

        // Setup
        hand(player1, turboBug, majesticManticore, snailHydra);
        hand(player2, ferretPacifier, urchinHurler);

        //Game start
        play(turboBug);

        play(urchinHurler);

        attack(turboBug, urchinHurler);

        play(ferretPacifier);

        play(snailHydra, player2);

        play(majesticManticore);

        AttackService.declareAttack(snailHydra, game); //Cannot be blocked

        AttackService.declareAttack(majesticManticore, game); // Ferret pacifier should be destroyed instead of Urchin Hurler

        assertFalse(player2.getDiscardPile().contains(urchinHurler));
        assertTrue(player2.getDiscardPile().contains(ferretPacifier));
    }
}
