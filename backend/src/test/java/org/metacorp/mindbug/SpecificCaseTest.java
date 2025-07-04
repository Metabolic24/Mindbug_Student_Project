package org.metacorp.mindbug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.AttackService;

import static org.junit.jupiter.api.Assertions.*;
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

    // #20
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

    // #21
    @Test
    public void goreagleBadlyReviveHyenix() throws GameStateException {
        CardInstance goreagleAlpha = getCardById(38);
        CardInstance tigerSquirrel = getCardById(29);
        CardInstance hyenix = getCardById(41);
        CardInstance explosiveToad = getCardById(8);

        // Setup
        hand(player1, hyenix, tigerSquirrel, goreagleAlpha);
        hand(player2, explosiveToad);

        //Game start
        play(hyenix, player2);

        play(tigerSquirrel);

        play(explosiveToad);

        play(goreagleAlpha);

        attack(explosiveToad, goreagleAlpha);

        chooseTargets(tigerSquirrel);

        AttackService.declareAttack(goreagleAlpha, game);

        choose(true);

        assertNotNull(game.getChoice());
    }

    // #23
    @Test
    public void strangeBarrelDoesNotTrigger() throws GameStateException {
        CardInstance strangeBarrel = getCardById(28);
        CardInstance spiderOwl = getCardById(27);
        CardInstance goreagleAlpha = getCardById(38);
        CardInstance tigerSquirrel = getCardById(29);
        CardInstance explosiveToad = getCardById(8);


        // Setup
        hand(player1, strangeBarrel, spiderOwl, goreagleAlpha, tigerSquirrel, explosiveToad);
        hand(player2);

        //Game start
        play(strangeBarrel, player2);

        play(spiderOwl);

        attack(strangeBarrel, spiderOwl);

        assertNull(game.getChoice());
        assertEquals(1, player1.getHand().size());
        assertEquals(2, player2.getHand().size());
    }

    // #26
    @Test
    public void goblinWerewolfNotDestroyedByMajesticManticore() throws GameStateException {
        CardInstance goblinWerewolf = getCardById(11);
        CardInstance hyenix = getCardById(41);
        CardInstance hungryHungryHamster = getCardById(40);
        CardInstance froblinInstigator = getCardById(37);
        CardInstance explosiveToad = getCardById(8);
        CardInstance majesticManticore = getCardById(42);

        // Setup
        hand(player1, goblinWerewolf, hyenix, hungryHungryHamster);
        hand(player2, froblinInstigator, explosiveToad, majesticManticore);

        //Game start
        play(goblinWerewolf);

        play(explosiveToad);

        play(hyenix);

        play(majesticManticore);

        play(hungryHungryHamster);

        choose(true);

        AttackService.declareAttack(majesticManticore, game);
        assertTrue(player1.getDiscardPile().contains(goblinWerewolf));
        assertTrue(player1.getDiscardPile().contains(hungryHungryHamster));
        assertTrue(player1.getBoard().contains(hyenix));

        assertEquals(majesticManticore, game.getAttackingCard());
    }
}
