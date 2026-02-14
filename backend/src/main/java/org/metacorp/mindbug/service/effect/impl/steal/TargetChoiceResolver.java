package org.metacorp.mindbug.service.effect.impl.steal;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.StealEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.effect.impl.StealEffectResolver;

import java.util.List;

public class TargetChoiceResolver extends StealEffectResolver implements ResolvableEffect<List<CardInstance>> {

    private final Player newOwner;

    private final CardInstance sourceCard;

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public TargetChoiceResolver(StealEffect effect, Player newOwner, CardInstance sourceCard) {
        super(effect, sourceCard);
        this.newOwner = newOwner;
        this.sourceCard = sourceCard;
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) throws GameStateException {
        if (chosenTargets != null) {
            stealCards(chosenTargets, game, newOwner, sourceCard);
        } else {
            throw new GameStateException("Unable to resolve target choice due to missing targets");
        }
    }
}
