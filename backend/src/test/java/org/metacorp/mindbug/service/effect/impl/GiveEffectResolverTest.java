package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GiveEffectResolverTest {

    private Game game;
    private CardInstance randomCard;
    private Player currentPlayer;
    private Player opponentPlayer;

    private GiveEffect effect;
    private GiveEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        PlayerService playerService = new PlayerService();
        game = StartService.newGame(new Player(playerService.createPlayer("Player1")), new Player(playerService.createPlayer("Player2")));
        randomCard = game.getCurrentPlayer().getHand().getFirst();
        currentPlayer = game.getCurrentPlayer();
        opponentPlayer = game.getOpponent().get(0);

        currentPlayer.addCardToBoard(randomCard);

        effect = new GiveEffect();
        effect.setType(EffectType.GIVE);
        effectResolver = new GiveEffectResolver(effect);
        timing = EffectTiming.PLAY;
    }

    @Test
    public void testBasic() {
        effect.setItself(true);
        effectResolver.apply(game, randomCard, timing);

        assertTrue(currentPlayer.getBoard().isEmpty());
        assertEquals(1, opponentPlayer.getBoard().size());
        assertTrue(opponentPlayer.getBoard().contains(randomCard));

        assertEquals(opponentPlayer, randomCard.getOwner());
    }
}
