package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCards;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for ForceAttackEffect
 */
public class ForceAttackEffectResolver extends EffectResolver<ForceAttackEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public ForceAttackEffectResolver(ForceAttackEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) throws GameStateException, WebSocketException {
        Player opponent = effectSource.getOwner().getOpponent(game.getPlayers());

        if (timing == EffectTiming.PASSIVE) {
            if (effect.getKeyword() != null) {
                boolean forcedAttack = false;
                for (CardInstance opponentCard : opponent.getBoard()) {
                    if (!opponentCard.hasKeyword(effect.getKeyword())) {
                        opponentCard.setAbleToAttack(false);
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
                    game.getLogger().debug("Player {} must choose a card that will be forced to attack (available targets : {})", getLoggablePlayer(effectSource.getOwner()), getLoggableCards(opponentBoard));
            }
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> choiceResolver) throws GameStateException, WebSocketException {
        resolve(game, choiceResolver.getFirst());
    }

    private void resolve(Game game, CardInstance attackingCard) throws GameStateException, WebSocketException {
        if (this.effect.isSingleTarget()) {
            game.setForcedTarget(effectSource);
        }

        game.getLogger().debug("{} forced to attack", getLoggableCard(attackingCard));

        AttackService.declareAttack(attackingCard, game);

        HistoryService.logEffect(game, effect.getType(), effectSource, Collections.singleton(attackingCard));
    }
}
