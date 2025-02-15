package org.metacorp.mindbug.effect.steal;

import org.metacorp.mindbug.effect.ResolvableEffect;
import org.metacorp.mindbug.model.choice.BooleanChoiceResolver;
import org.metacorp.mindbug.model.Game;

/**
 * Effect that steals card from the opponent hand or board depending on several conditions
 */
public class BooleanStealEffect extends StealEffect implements ResolvableEffect<BooleanChoiceResolver> {

    public BooleanStealEffect(StealEffect effect) {
        this.setValue(effect.getValue());
        this.setMin(effect.getMin());
        this.setMax(effect.getMax());
        this.setRandom(effect.isRandom());
        this.setMustPlay(effect.isMustPlay());
        this.setMayPlay(effect.isMayPlay());
        this.setSource(effect.getSource());
    }

    @Override
    public void resolve(Game game, BooleanChoiceResolver choiceResolver) {
        if (choiceResolver != null && choiceResolver.getChoice() != null) {
            if (choiceResolver.getChoice()) {
                choiceResolver.getPlayerToChoose().getBoard().add(choiceResolver.getCard());
            } else {
                choiceResolver.getPlayerToChoose().getHand().add(choiceResolver.getCard());
            }
        } else {
            //TODO Raise error
        }
    }
}
