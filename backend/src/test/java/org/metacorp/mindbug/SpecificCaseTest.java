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
import org.metacorp.mindbug.utils.TestGameUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metacorp.mindbug.utils.TestGameUtils.attack;
import static org.metacorp.mindbug.utils.TestGameUtils.choose;
import static org.metacorp.mindbug.utils.TestGameUtils.chooseTargets;
import static org.metacorp.mindbug.utils.TestGameUtils.getCardById;
import static org.metacorp.mindbug.utils.TestGameUtils.getCardsById;
import static org.metacorp.mindbug.utils.TestGameUtils.hand;
import static org.metacorp.mindbug.utils.TestGameUtils.huntTarget;
import static org.metacorp.mindbug.utils.TestGameUtils.play;
import static org.metacorp.mindbug.utils.TestGameUtils.prepareCustomGame;
import static org.metacorp.mindbug.utils.TestUtils.cleanHistoryDirectory;

public class SpecificCaseTest {

    private Game game;

    private Player player1;
    private Player player2;

    @BeforeEach
    public void setup() {
        game = prepareCustomGame();
        player1 = TestGameUtils.getPlayer1();
        player2 = TestGameUtils.getPlayer2();
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

        // Start game
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

        // Start game
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

        // Start game
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

        // Start game
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

    // #26
    @Test
    public void opponentCanChooseBlockTargetWhileCannotBlock() throws GameStateException {
        CardInstance gorillion = getCardById(12);
        CardInstance ferretPacifier = getCardById(36);
        CardInstance explosiveToad = getCardById(8);
        CardInstance hyenix = getCardById(41);
        CardInstance goreagleAlpha = getCardById(38);

        // Setup
        hand(player1, ferretPacifier, hyenix, gorillion);
        hand(player2, goreagleAlpha, explosiveToad);

        // Start game
        play(hyenix, player2);

        play(ferretPacifier);

        play(explosiveToad, player1);

        play(goreagleAlpha, player1);

        attack(hyenix, goreagleAlpha);

        choose(true);

        AttackService.resolveAttack(null, game);

        play(gorillion);

        attack(hyenix, gorillion);

        AttackService.declareAttack(goreagleAlpha, game);

        choose(true);

        choose(true);

        huntTarget(null);

        assertNull(game.getAttackingCard());
        assertEquals(game.getCurrentPlayer(), player2);
        assertEquals(1, player2.getTeam().getLifePoints());
    }

    // #27
    @Test
    public void hamsterLionDoesNotCauseGameEndWhenNoOneCanAttack() throws GameStateException, IOException {
        CardInstance hyenix = getCardById(41);
        CardInstance graveRobber = getCardById(13);
        CardInstance hamsterLion = getCardById(39);
        CardInstance snailHydra = getCardById(25);

        // Setup
        hand(player1, hamsterLion, snailHydra);
        hand(player2, hyenix, graveRobber);

        // Start game
        play(hamsterLion);

        play(hyenix);

        play(snailHydra);

        play(graveRobber);

        attack(snailHydra, null);

        assertTrue(game.isFinished());

        cleanHistoryDirectory();
    }

    @Test
    public void urchinHurlerCanAttackWithShieldBugsBoostWhileHamsterLion() throws GameStateException {
        CardInstance hamsterLion = getCardById(39);
        CardInstance shieldBugs = getCardById(24);
        CardInstance urchinHurler = getCardById(32);
        CardInstance hyenix = getCardById(41);

        // Setup
        hand(player1, urchinHurler, shieldBugs, hyenix);

        hand(player2, hamsterLion);

        // Start game
        play(urchinHurler);

        play(hamsterLion);

        play(shieldBugs);

        attack(hamsterLion, shieldBugs);

        choose(false);

        assertFalse(game.isFinished());
        assertFalse(urchinHurler.isAbleToAttack());
        assertFalse(shieldBugs.isAbleToAttack());
    }

    @Test
    public void compostDragonRevivesCompostDragon() throws GameStateException {
        List<CardInstance> compostDragons = getCardsById(5);
        assertEquals(2, compostDragons.size());

        CardInstance compostDragon1 = compostDragons.get(0);
        CardInstance compostDragon2 = compostDragons.get(1);
        CardInstance hyenix = getCardById(41);
        CardInstance ferretPacifier = getCardById(36);
        CardInstance tigerSquirrel = getCardById(29);
        CardInstance snailHydra = getCardById(25);

        hand(player1, compostDragon1, compostDragon2, ferretPacifier, snailHydra);
        hand(player2, hyenix, tigerSquirrel);

        play(snailHydra);

        play(tigerSquirrel);

        play(ferretPacifier);

        play(hyenix);

        attack(ferretPacifier, tigerSquirrel);

        attack(hyenix, ferretPacifier);

        choose(true);

        play(compostDragon1);

        chooseTargets(ferretPacifier);

        attack(hyenix, compostDragon1);

        choose(true);

        AttackService.resolveAttack(ferretPacifier, game);

        play(compostDragon2);

        chooseTargets(compostDragon1);

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
