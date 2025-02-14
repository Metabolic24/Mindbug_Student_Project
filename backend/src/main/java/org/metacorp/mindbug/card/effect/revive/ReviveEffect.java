package org.metacorp.mindbug.card.effect.revive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.choice.bool.BooleanChoice;
import org.metacorp.mindbug.choice.bool.BooleanChoiceResolver;
import org.metacorp.mindbug.player.Player;

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
        game.setCurrentChoice(new BooleanChoice(card.getOwner(), card, this));
    }

    @Override
    public void resolve(Game game, BooleanChoiceResolver choiceResolver) {
        if (choiceResolver.getChoice()) {
            CardInstance card = choiceResolver.getCard();
            Player player = choiceResolver.getPlayerToChoose();

            card.getOwner().getDiscardPile().remove(card);
            player.getBoard().add(card);
            card.setOwner(player);

            game.addEffectsToQueue(card, EffectTiming.PLAY);
        }
    }
}
