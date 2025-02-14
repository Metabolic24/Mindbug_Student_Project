package org.metacorp.mindbug.card.effect.steal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.game.Game;

import java.util.List;

/**
 * Effect that steals card from the opponent hand or board depending on several conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TargetStealEffect extends StealEffect implements ResolvableEffect<List<CardInstance>> {

    private Game game; //TODO A voir si on peut pas gérer le game autrement (en paramètre de resolve peut-être)

    public TargetStealEffect(StealEffect effect, Game game) {
        this.setValue(effect.getValue());
        this.setMin(effect.getMin());
        this.setMax(effect.getMax());
        this.setRandom(effect.isRandom());
        this.setMustPlay(effect.isMustPlay());
        this.setMayPlay(effect.isMayPlay());
        this.setSource(effect.getSource());
        this.setGame(game);
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        if (chosenTargets != null) {
            stealCards(chosenTargets, game);
        } else {
            // TODO Unexpected resolver value (raise error?)
        }
    }
}
