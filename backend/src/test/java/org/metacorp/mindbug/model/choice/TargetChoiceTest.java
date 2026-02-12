package org.metacorp.mindbug.model.choice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.StartService;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TargetChoiceTest {
    private Game game;
    private Player currentPlayer;
    private Player opponent;
    private ResolvableEffect<List<CardInstance>> effect;

    @BeforeEach
    public void initGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        opponent = game.getOpponent().get(0);
        effect = (game, choiceResolver) -> {
            for (CardInstance card : choiceResolver) {
                card.getOwner().addCardToDiscardPile(card);
            }
        };
    }

    @Test
    public void testResolve() {
        DestroyEffect destroyEffect = new DestroyEffect();
        CardInstance currentCard = currentPlayer.getHand().removeFirst();
        currentCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(destroyEffect));
        currentPlayer.getDiscardPile().add(currentCard);

        GainEffect defendEffect = new GainEffect();
        CardInstance opponentCard = opponent.getHand().getFirst();
        opponentCard.getCard().getEffects().put(EffectTiming.DEFEATED, List.of(defendEffect));
        opponent.addCardToBoard(opponentCard);
        opponent.addCardToBoard(opponent.getHand().getFirst());

        TargetChoice choice = new TargetChoice(currentPlayer, currentCard, effect, 1, new HashSet<>(opponent.getBoard()));
        game.setChoice(choice);

        choice.resolve(List.of(opponentCard.getUuid()), game);

        assertNull(game.getChoice());
        assertEquals(1, opponent.getDiscardPile().size());
        assertTrue(opponent.getDiscardPile().contains(opponentCard));
    }
}
