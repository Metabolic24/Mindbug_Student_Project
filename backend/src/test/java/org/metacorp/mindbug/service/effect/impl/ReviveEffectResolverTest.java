package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.service.StartService;
import org.metacorp.mindbug.model.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class ReviveEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private ReviveEffect effect;
    private ReviveEffectResolver effectResolver;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame("Player1", "Player2");
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
        randomCard = opponentPlayer.getHand().removeFirst();
        opponentPlayer.getDiscardPile().add(randomCard);

        effect = new ReviveEffect();
        effectResolver = new ReviveEffectResolver(effect);
    }

    //TODO Test choice resolution

    @Test
    public void testBasic() {
        effectResolver.apply(game, randomCard);

        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.BOOLEAN, game.getChoice().getType());

        BooleanChoice booleanChoice = (BooleanChoice) game.getChoice();

        assertEquals(effectResolver, booleanChoice.getEffectResolver());
        assertEquals(randomCard, booleanChoice.getCard());
        assertEquals(opponentPlayer, booleanChoice.getPlayerToChoose());
    }
}
