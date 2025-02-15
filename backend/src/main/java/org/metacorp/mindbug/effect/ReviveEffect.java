package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.service.EffectQueueService;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.BooleanChoiceResolver;
import org.metacorp.mindbug.model.player.Player;

/**
 * Effect that revives the current card on some specific conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReviveEffect extends AbstractEffect implements ResolvableEffect<BooleanChoiceResolver> {
    public final static String TYPE = "REVIVE";

    @Override
    public void apply(Game game, CardInstance card) {
        game.setChoice(new BooleanChoice(card.getOwner(), card, this));
    }

    @Override
    public void resolve(Game game, BooleanChoiceResolver choiceResolver) {
        if (choiceResolver.getChoice()) {
            CardInstance card = choiceResolver.getCard();
            Player player = choiceResolver.getPlayerToChoose();

            card.getOwner().getDiscardPile().remove(card);
            player.getBoard().add(card);
            card.setOwner(player);

            EffectQueueService.addEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());
        }
    }
}
