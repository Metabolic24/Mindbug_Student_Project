package org.metacorp.mindbug.card.effect.steal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.choice.bool.BooleanChoiceResolver;

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
    public void resolve(BooleanChoiceResolver choiceResolver) {
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
