package org.metacorp.mindbug.service.effect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.EvolveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvolveEffectResolverTest {

    private Game game;
    private CardInstance evolvingCard;
    private CardInstance evolutionCard;
    private Player currentPlayer;

    private EvolveEffectResolver effectResolver;
    private EffectTiming timing;

    @BeforeEach
    public void prepareGame() {
        game = StartService.newGame(new Player(PlayerService.createPlayer("Player1")), new Player(PlayerService.createPlayer("Player2")));
        currentPlayer = game.getCurrentPlayer();
        evolvingCard = currentPlayer.getHand().getFirst();

        evolutionCard = currentPlayer.getHand().get(1);
        evolutionCard.getCard().setEvolution(true);
        game.getEvolutionCards().add(evolutionCard);

        EvolveEffect effect = new EvolveEffect();
        effect.setId(evolutionCard.getCard().getId());
        effectResolver = new EvolveEffectResolver(effect);
        timing = EffectTiming.ACTION;
    }

    @Test
    public void testBasic_firstEvolution() {
        currentPlayer.addCardToBoard(evolvingCard);

        effectResolver.apply(game, evolvingCard, timing);

        assertFalse(currentPlayer.getBoard().contains(evolvingCard));
        assertFalse(currentPlayer.getDiscardPile().contains(evolvingCard));
        assertTrue(currentPlayer.getBoard().contains(evolutionCard));
    }

    @Test
    public void testBasic_secondEvolutionTough() {
        currentPlayer.addCardToBoard(evolvingCard);
        evolvingCard.getKeywords().add(CardKeyword.TOUGH);
        evolvingCard.setStillTough(false);

        evolutionCard.getKeywords().add(CardKeyword.TOUGH);
        evolutionCard.setStillTough(true);

        effectResolver.apply(game, evolvingCard, timing);

        assertFalse(currentPlayer.getBoard().contains(evolvingCard));
        assertFalse(currentPlayer.getDiscardPile().contains(evolvingCard));
        assertTrue(currentPlayer.getBoard().contains(evolutionCard));
        assertFalse(evolutionCard.isStillTough());
    }
}
