package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReviveEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player opponentPlayer;

    private ReviveEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        opponentPlayer = game.getCurrentPlayer().getOpponent(game.getPlayers());
        randomCard = opponentPlayer.getHand().removeFirst();
        opponentPlayer.getDiscardPile().add(randomCard);

        ReviveEffect effect = new ReviveEffect();
        effect.setType(EffectType.REVIVE);
        effectResolver = new ReviveEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    //TODO Test choice resolution

    @Test
    public void testBasic() {
        effectResolver.apply(game, randomCard, timing);

        assertTrue(opponentPlayer.getDiscardPile().contains(randomCard));

        assertNotNull(game.getChoice());
        assertEquals(ChoiceType.BOOLEAN, game.getChoice().getType());

        BooleanChoice booleanChoice = (BooleanChoice) game.getChoice();

        assertEquals(effectResolver, booleanChoice.getEffectResolver());
        assertEquals(randomCard, booleanChoice.getSourceCard());
        assertEquals(opponentPlayer, booleanChoice.getPlayerToChoose());
    }
}
