package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.ForceAttackEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Effect resolver for ForceAttackEffect
 */
public class ForceAttackEffectResolver extends EffectResolver<ForceAttackEffect> implements ResolvableEffect<List<CardInstance>> {

    private CardInstance effectSource;

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public ForceAttackEffectResolver(ForceAttackEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance effectSource, EffectTiming timing) {
        this.effectSource = effectSource;

      
        Player opponent = AppUtils.ChosenOpponent( game, effectSource.getOwner());

        if (timing == EffectTiming.PASSIVE) {
            if (effect.getKeyword() != null) {
                boolean forcedAttack = false;
                for (CardInstance card : opponent.getBoard()) {
                    if (!card.hasKeyword(effect.getKeyword())) {
                        card.setAbleToAttack(false);
                    } else {
                        forcedAttack = true;
                    }
                }

                game.setForcedAttack(forcedAttack);

                if (forcedAttack && this.effect.isSingleTarget()) {
                    game.setForcedTarget(effectSource);
                }
            }
        } else {
            List<CardInstance> opponentBoard = opponent.getBoard();
            switch (opponentBoard.size()) {
                case 0:
                    // Nothing to do
                    break;
                case 1:
                    resolve(game, opponentBoard.getFirst());
                    break;
                default:
                    game.setChoice(new TargetChoice(effectSource.getOwner(), effectSource, this, 1, new HashSet<>(opponentBoard)));
            }
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> choiceResolver) {
        resolve(game, choiceResolver.getFirst());
    }

    private void resolve(Game game, CardInstance attackingCard) {
        if (this.effect.isSingleTarget()) {
            game.setForcedTarget(effectSource);
        }

        try {
            AttackService.declareAttack(attackingCard, game);
        } catch (GameStateException e) {
            // TODO Manage error
            e.printStackTrace();
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, Collections.singleton(attackingCard));
    }
}
