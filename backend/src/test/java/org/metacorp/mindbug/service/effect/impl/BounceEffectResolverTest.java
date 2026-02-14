package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.BounceEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BounceEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private BounceEffect effect;
    private BounceEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.startGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        Player currentPlayer = game.getCurrentPlayer();
        opponentPlayer = currentPlayer.getOpponent(game.getPlayers());

        randomCard = currentPlayer.getHand().getFirst();
        randomCard.setStillTough(false);
        currentPlayer.addCardToBoard(randomCard);

        effect = new BounceEffect();
        effect.setType(EffectType.BOUNCE);
        effectResolver = new BounceEffectResolver(effect, randomCard);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void test_nominal() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setValue(1);

        effectResolver.apply(game, timing);
        assertTrue(opponentPlayer.getBoard().isEmpty());
        assertEquals(5, opponentPlayer.getHand().size());
        assertNull(game.getChoice());
    }

    @Test
    public void test_choice() {
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());
        opponentPlayer.addCardToBoard(opponentPlayer.getHand().getFirst());

        effect.setValue(1);

        effectResolver.apply(game, timing);
        assertEquals(2, opponentPlayer.getBoard().size());
        assertEquals(3, opponentPlayer.getHand().size());

        IChoice<?> choice = game.getChoice();
        assertNotNull(choice);
        assertEquals(ChoiceType.TARGET, choice.getType());

        TargetChoice targetChoice = (TargetChoice) choice;
        assertNotNull(targetChoice.getEffect());
        assertEquals(effect, ((BounceEffectResolver) targetChoice.getEffect()).getEffect());
        assertEquals(randomCard, targetChoice.getEffectSource());
        assertEquals(randomCard.getOwner(), targetChoice.getPlayerToChoose());
        assertEquals(1, targetChoice.getTargetsCount());
        assertEquals(2, targetChoice.getAvailableTargets().size());
        assertFalse(targetChoice.isOptional());
    }
}
