package org.metacorp.mindbug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.utils.TestGameUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.metacorp.mindbug.utils.TestUtils.cleanHistoryDirectory;

public class SpecificCaseTest {

    private Game game;
    private TestGameUtils utils;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setup() {
        utils = new TestGameUtils();
        game = utils.prepareCustomGame();
        player1 = utils.getPlayer1();
        player2 = utils.getPlayer2();
    }

    // #20
    @Test
    public void hurlerAlwaysBoost() throws GameStateException {
        CardInstance turboBug =  utils.getCardById(30);
        CardInstance majesticManticore = utils.getCardById(42);
        CardInstance snailHydra = utils.getCardById(25);
        CardInstance ferretPacifier = utils.getCardById(36);
        CardInstance urchinHurler = utils.getCardById(32);

        // Setup
        utils.hand(player1, turboBug, majesticManticore, snailHydra);
        utils.hand(player2, ferretPacifier, urchinHurler);

        // Start game
        utils.play(turboBug);
        utils.play(urchinHurler);
       
        utils.attack(turboBug, urchinHurler);

        utils.play(ferretPacifier);

        utils.play(snailHydra, player2);

        utils.play(majesticManticore);

        AttackService.declareAttack(snailHydra, game); //Cannot be blocked

        AttackService.declareAttack(majesticManticore, game); // Ferret pacifier should be destroyed instead of Urchin Hurler

        assertFalse(player2.getDiscardPile().contains(urchinHurler));
        assertTrue(player2.getDiscardPile().contains(ferretPacifier));
    }

    // #21
    @Test
    public void goreagleBadlyReviveHyenix() throws GameStateException {
        CardInstance goreagleAlpha = utils.getCardById(38);
        CardInstance tigerSquirrel = utils.getCardById(29);
        CardInstance hyenix = utils.getCardById(41);
        CardInstance explosiveToad = utils.getCardById(8);

        // Setup
        utils.hand(player1, hyenix, tigerSquirrel, goreagleAlpha);
        utils.hand(player2, explosiveToad);

        // Start game
        utils.play(hyenix, player2);

        utils.play(tigerSquirrel);

        utils.play(explosiveToad);

        utils.play(goreagleAlpha);

        utils.attack(explosiveToad, goreagleAlpha);

        utils.chooseTargets(tigerSquirrel);

        AttackService.declareAttack(goreagleAlpha, game);

        utils.choose(true);

        assertNotNull(game.getChoice());
    }

    // #23
    @Test
    public void strangeBarrelDoesNotTrigger() throws GameStateException {
        CardInstance strangeBarrel = utils.getCardById(28);
        CardInstance spiderOwl = utils.getCardById(27);
        CardInstance goreagleAlpha = utils.getCardById(38);
        CardInstance tigerSquirrel = utils.getCardById(29);
        CardInstance explosiveToad = utils.getCardById(8);


        // Setup
        utils.hand(player1, strangeBarrel, spiderOwl, goreagleAlpha, tigerSquirrel, explosiveToad);
        utils.hand(player2);

        // Start game
        utils.play(strangeBarrel, player2);

        utils.play(spiderOwl);

        utils.attack(strangeBarrel, spiderOwl);

        assertNull(game.getChoice());
        assertEquals(1, player1.getHand().size());
        assertEquals(2, player2.getHand().size());
    }

    // #26
    @Test
    public void goblinWerewolfNotDestroyedByMajesticManticore() throws GameStateException {
        CardInstance goblinWerewolf = utils.getCardById(11);
        CardInstance hyenix = utils.getCardById(41);
        CardInstance hungryHungryHamster = utils.getCardById(40);
        CardInstance froblinInstigator = utils.getCardById(37);
        CardInstance explosiveToad = utils.getCardById(8);
        CardInstance majesticManticore = utils.getCardById(42);

        // Setup
        utils.hand(player1, goblinWerewolf, hyenix, hungryHungryHamster);
        utils.hand(player2, froblinInstigator, explosiveToad, majesticManticore);
        
        // Start game
        utils.play(goblinWerewolf);

        utils.play(explosiveToad);

        utils.play(hyenix);

        utils.play(majesticManticore);
        utils.choose(true);

        utils.play(hungryHungryHamster);

        utils.choose(true);

        AttackService.declareAttack(majesticManticore, game);
        assertTrue(player1.getDiscardPile().contains(goblinWerewolf));
        assertTrue(player1.getDiscardPile().contains(hungryHungryHamster));
        assertTrue(player1.getBoard().contains(hyenix));

        assertEquals(majesticManticore, game.getAttackingCard());
    }

    // #26
    @Test
    public void opponentCanChooseBlockTargetWhileCannotBlock() throws GameStateException {
        CardInstance gorillion = utils.getCardById(12);
        CardInstance ferretPacifier = utils.getCardById(36);
        CardInstance explosiveToad = utils.getCardById(8);
        CardInstance hyenix = utils.getCardById(41);
        CardInstance goreagleAlpha = utils.getCardById(38);

        // Setup
        utils.hand(player1, ferretPacifier, hyenix, gorillion);
        utils.hand(player2, goreagleAlpha, explosiveToad);

        // Start game
        utils.play(hyenix);

        utils.play(ferretPacifier);

        utils.play(explosiveToad, player1);

        utils.play(goreagleAlpha, player1);

        utils.attack(hyenix, goreagleAlpha);

        utils.choose(true);

        AttackService.resolveAttack(null, game);

        utils.play(gorillion);

        utils.attack(hyenix, gorillion);

        AttackService.declareAttack(goreagleAlpha, game);

        utils.choose(true);

        utils.choose(true);

        utils.huntTarget(null);

        assertNull(game.getAttackingCard());
        assertEquals(game.getCurrentPlayer(), player2);
        assertEquals(1, player2.getTeam().getLifePoints());
    }

    // #27
    @Test
    public void hamsterLionDoesNotCauseGameEndWhenNoOneCanAttack() throws GameStateException, IOException {
        CardInstance hyenix = utils.getCardById(41);
        CardInstance graveRobber = utils.getCardById(13);
        CardInstance hamsterLion = utils.getCardById(39);
        CardInstance snailHydra = utils.getCardById(25);

        // Setup
        utils.hand(player1, hamsterLion, snailHydra);
        utils.hand(player2, hyenix, graveRobber);

        // Start game
        utils.play(hamsterLion);

        utils.play(hyenix);

        utils.play(snailHydra);

        utils.play(graveRobber);

        utils.attack(snailHydra, null);

        assertTrue(game.isFinished());

        cleanHistoryDirectory();
    }

    @Test
    public void urchinHurlerCanAttackWithShieldBugsBoostWhileHamsterLion() throws GameStateException {
        CardInstance hamsterLion = utils.getCardById(39);
        CardInstance shieldBugs = utils.getCardById(24);
        CardInstance urchinHurler = utils.getCardById(32);
        CardInstance hyenix = utils.getCardById(41);

        // Setup
        utils.hand(player1, urchinHurler, shieldBugs, hyenix);

        utils.hand(player2, hamsterLion);

        // Start game
        utils.play(urchinHurler);

        utils.play(hamsterLion);

        utils.play(shieldBugs);

        utils.attack(hamsterLion, shieldBugs);

        utils.choose(false);

        assertFalse(game.isFinished());
        assertFalse(urchinHurler.isAbleToAttack());
        assertFalse(shieldBugs.isAbleToAttack());
    }

    @Test
    public void compostDragonRevivesCompostDragon() throws GameStateException {
        List<CardInstance> compostDragons = utils.getCardsById(5);
        assertEquals(2, compostDragons.size());

        CardInstance compostDragon1 = compostDragons.get(0);
        CardInstance compostDragon2 = compostDragons.get(1);
        CardInstance hyenix = utils.getCardById(41);
        CardInstance ferretPacifier = utils.getCardById(36);
        CardInstance tigerSquirrel = utils.getCardById(29);
        CardInstance snailHydra = utils.getCardById(25);

        utils.hand(player1, compostDragon1, compostDragon2, ferretPacifier, snailHydra);
        utils.hand(player2, hyenix, tigerSquirrel);

        utils.play(snailHydra);

        utils.play(tigerSquirrel);

        utils.play(ferretPacifier);

        utils.play(hyenix);

        utils.attack(ferretPacifier, tigerSquirrel);

        utils.attack(hyenix, ferretPacifier);

        utils.choose(true);

        utils.play(compostDragon1);

        utils.chooseTargets(ferretPacifier);

        utils.attack(hyenix, compostDragon1);

        utils.choose(true);

        AttackService.resolveAttack(ferretPacifier, game);

        utils.play(compostDragon2);

        utils.chooseTargets(compostDragon1);

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.TARGET, game.getChoice().getType());

        TargetChoice targetChoice = (TargetChoice) game.getChoice();

        assertNotNull(targetChoice.getEffect());
        assertEquals(player1, targetChoice.getPlayerToChoose());
        assertEquals(compostDragon1, targetChoice.getEffectSource());
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(2, targetChoice.getAvailableTargets().size());
        assertTrue(targetChoice.getAvailableTargets().contains(ferretPacifier));
        assertTrue(targetChoice.getAvailableTargets().contains(snailHydra));
    }
}
