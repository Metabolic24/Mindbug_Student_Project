package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.ForceAttackEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ForceAttackEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private ForceAttackEffectResolver effectResolver;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.setStillTough(false);
        randomCard.getEffects(EffectTiming.PASSIVE).clear();
        currentPlayer.addCardToBoard(randomCard);

        ForceAttackEffect effect = new ForceAttackEffect();
        effect.setType(EffectType.FORCE_ATTACK);
        effectResolver = new ForceAttackEffectResolver(effect);
    }

    @Test
    public void testApply_nominalAction() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard, EffectTiming.ACTION);

        assertFalse(game.isForcedAttack());

        TargetChoice choice = assertInstanceOf(TargetChoice.class, game.getChoice());
        assertEquals(effectResolver, choice.getEffect());
        assertEquals(randomCard, choice.getEffectSource());
        assertEquals(3, choice.getAvailableTargets().size());
        assertEquals(1, choice.getTargetsCount());
        assertEquals(ChoiceType.TARGET, choice.getType());
        assertEquals(game.getCurrentPlayer(), choice.getPlayerToChoose());
    }

    @Test
    public void testApply_noChoice() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        CardInstance selectedCard = opponentPlayer.getHand().getFirst();
        selectedCard.getKeywords().remove(CardKeyword.HUNTER);
        selectedCard.getKeywords().remove(CardKeyword.SNEAKY);
        selectedCard.getEffects(EffectTiming.ATTACK).clear();
        opponentPlayer.addCardToBoard(selectedCard);

        effectResolver.apply(game, randomCard, EffectTiming.ACTION);

        assertNull(game.getForcedTarget());
        assertFalse(game.isForcedAttack());
        assertEquals(selectedCard, game.getAttackingCard());
        assertNull(game.getChoice());
    }

    @Test
    public void testApply_noEffect() {
        currentPlayer.addCardToBoard(currentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard, EffectTiming.ACTION);

        assertNull(game.getForcedTarget());
        assertFalse(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());
    }

    @Test
    public void testResolve_nominalActionSneaky() {
        randomCard.getKeywords().remove(CardKeyword.SNEAKY);

        CardInstance selectedCard = opponentPlayer.getHand().getFirst();
        selectedCard.getKeywords().remove(CardKeyword.HUNTER);
        selectedCard.setAbleToAttackTwice(false);
        selectedCard.getKeywords().add(CardKeyword.SNEAKY);
        selectedCard.getEffects(EffectTiming.ATTACK).clear();
        opponentPlayer.addCardToBoard(selectedCard);
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.resolve(game, Collections.singletonList(selectedCard));

        assertNull(game.getForcedTarget());
        assertFalse(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());
        assertEquals(2, currentPlayer.getTeam().getLifePoints());
    }

    @Test
    public void testApply_passiveButNoKeyword() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effectResolver.apply(game, randomCard, EffectTiming.PASSIVE);

        assertNull(game.getForcedTarget());
        assertFalse(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertTrue(card.isAbleToAttack());
        }
    }

    @Test
    public void testApply_nominalPassive() {
        for (int i = 0; i < 3; i++) {
            CardInstance opponentCard = opponentPlayer.getHand().getFirst();
            opponentCard.getKeywords().remove(CardKeyword.SNEAKY);

            if (i != 1) {
                opponentCard.getKeywords().add(CardKeyword.HUNTER);
            } else {
                opponentCard.getKeywords().remove(CardKeyword.HUNTER);
            }

            opponentPlayer.addCardToBoard(opponentCard);
        }

        effectResolver.getEffect().setKeyword(CardKeyword.HUNTER);
        effectResolver.apply(game, randomCard, EffectTiming.PASSIVE);

        assertNull(game.getForcedTarget());
        assertTrue(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(card.hasKeyword(CardKeyword.HUNTER), card.isAbleToAttack());
        }
    }

    @Test
    public void testApply_nominalPassiveWithSingleTarget() {
        for (int i = 0; i < 3; i++) {
            CardInstance opponentCard = opponentPlayer.getHand().getFirst();
            opponentCard.getKeywords().remove(CardKeyword.SNEAKY);

            if (i != 1) {
                opponentCard.getKeywords().add(CardKeyword.HUNTER);
            } else {
                opponentCard.getKeywords().remove(CardKeyword.HUNTER);
            }

            opponentPlayer.addCardToBoard(opponentCard);
        }

        effectResolver.getEffect().setKeyword(CardKeyword.HUNTER);
        effectResolver.getEffect().setSingleTarget(true);
        effectResolver.apply(game, randomCard, EffectTiming.PASSIVE);

        assertEquals(randomCard, game.getForcedTarget());
        assertTrue(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertEquals(card.hasKeyword(CardKeyword.HUNTER), card.isAbleToAttack());
        }
    }

    @Test
    public void testApply_nominalPassiveWithoutKeyword() {
        for (int i = 0; i < 3; i++) {
            CardInstance opponentCard = opponentPlayer.getHand().getFirst();
            opponentCard.getKeywords().remove(CardKeyword.SNEAKY);

            if (i != 1) {
                opponentCard.getKeywords().add(CardKeyword.HUNTER);
            } else {
                opponentCard.getKeywords().remove(CardKeyword.HUNTER);
            }

            opponentPlayer.addCardToBoard(opponentCard);
        }

        effectResolver.getEffect().setSingleTarget(true);
        effectResolver.apply(game, randomCard, EffectTiming.PASSIVE);

        assertNull(game.getForcedTarget());
        assertFalse(game.isForcedAttack());
        assertNull(game.getAttackingCard());
        assertNull(game.getChoice());

        for (CardInstance card : opponentPlayer.getBoard()) {
            assertTrue(card.isAbleToAttack());
        }
    }
}


