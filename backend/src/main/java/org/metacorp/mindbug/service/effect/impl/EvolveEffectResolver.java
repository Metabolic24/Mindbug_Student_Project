package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.EvolveEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;
import org.metacorp.mindbug.service.game.EffectQueueService;

import java.util.Optional;

/**
 * Effect resolver for EvolveEffect
 */
public class EvolveEffectResolver extends GenericEffectResolver<EvolveEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public EvolveEffectResolver(EvolveEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance effectSource, EffectTiming timing) {
        Optional<CardInstance> relatedEvolutionCard = game.getEvolutionCards().stream().filter(cardInstance -> cardInstance.getCard().getId() == effect.getId()).findFirst();
        relatedEvolutionCard.ifPresent(evolutionCard -> {
            Player currentPlayer = game.getCurrentPlayer();
            evolutionCard.setOwner(currentPlayer);

            // Store the initial card in the evolved one so we can put it in the discard pile when destroyed
            if (effectSource.getCard().isEvolution()) {
                evolutionCard.setInitialCard(effectSource.getInitialCard());
            } else {
                evolutionCard.setInitialCard(effectSource);
            }

            // Keep the TOUGH status between the source and its evolution
            if (effectSource.hasKeyword(CardKeyword.TOUGH) && evolutionCard.hasKeyword(CardKeyword.TOUGH)) {
                evolutionCard.setStillTough(effectSource.isStillTough());
            }

            currentPlayer.getBoard().remove(effectSource);
            currentPlayer.addCardToBoard(evolutionCard);

            // Add PLAY effects (if any) if player is allowed to trigger them
            EffectQueueService.addBoardEffectsToQueue(evolutionCard, EffectTiming.PLAY, game.getEffectQueue());
        });
    }
}
