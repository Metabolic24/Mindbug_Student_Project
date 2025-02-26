package org.metacorp.mindbug.service;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.AbstractEffectResolver;

import java.util.*;

public class GameService {

    public static <T> void  resolveChoice(T data, Game game) throws GameStateException {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            throw new GameStateException("no choice to be resolved", Map.of("data", data));
        } else if (data == null) {
            throw new GameStateException("invalid data for choice resolution", Map.of("choice", choice));
        }

        try {
            ((IChoice<T>)choice).resolve(data, game);

            GameService.refreshGameState(game);
            EffectQueueService.resolveEffectQueue(true, game);
        } catch (ClassCastException e) {
            throw new GameStateException("invalid choice resolution", e, Map.of("choice", choice, "data", data));
        }
    }

    public static void lifePointLost(Player player, Game game) {
        if (player.getTeam().getLifePoints() <= 0) {
            endGame(player, game);
            return;
        }

        for (CardInstance card : player.getBoard()) {
            EffectQueueService.addEffectsToQueue(card, EffectTiming.LIFE_LOST, game.getEffectQueue());
        }

        //TODO Il faudrait être capable de différencier un effet LIFE_LOST qui s'active uniquement dans la défausse d'un effet qui s'active uniquement sur le terrain
        for (CardInstance card : player.getDiscardPile()) {
            EffectQueueService.addEffectsToQueue(card, EffectTiming.LIFE_LOST, game.getEffectQueue());
        }
    }

    public static void endGame(Player loser, Game game) {
        Player winner = loser.getOpponent(game.getPlayers());
        System.out.printf("%s wins ; %s loses\n", winner.getName(), loser.getName());

        game.setFinished(true);
    }

    public static void defeatCard(CardInstance card, Queue<EffectsToApply> effectQueue) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            card.getOwner().addCardToDiscardPile(card);
            EffectQueueService.addEffectsToQueue(card, EffectTiming.DEFEATED, effectQueue);
        }
    }

    public static void refreshGameState(Game game) {
        List<EffectsToApply> powerUpEffects = new ArrayList<>();
        List<EffectsToApply> otherEffects = new ArrayList<>();

        for (Player player: game.getPlayers()) {
            player.refresh();

            for (CardInstance cardInstance : player.getBoard()) {
                List<AbstractEffect> cardEffects = cardInstance.getEffects(EffectTiming.PASSIVE);
                for (AbstractEffect cardEffect : cardEffects) {
                    EffectsToApply effectToApply = new EffectsToApply(Collections.singletonList(cardEffect), cardInstance);
                    if (cardEffect.getType() == EffectType.POWER_UP) {
                        powerUpEffects.add(effectToApply);
                    } else {
                        otherEffects.add(effectToApply);
                    }
                }
            }

            for (CardInstance cardInstance : player.getDiscardPile()) {
                List<AbstractEffect> cardEffects = cardInstance.getEffects(EffectTiming.DISCARD);
                for (AbstractEffect cardEffect : cardEffects) {
                    EffectsToApply effectToApply = new EffectsToApply(Collections.singletonList(cardEffect), cardInstance);
                    if (cardEffect.getType() == EffectType.POWER_UP) {
                        powerUpEffects.add(effectToApply);
                    } else {
                        otherEffects.add(effectToApply);
                    }
                }
            }
        }

        for (EffectsToApply effect : powerUpEffects) {
            effect.getEffects().forEach(effectToApply -> AbstractEffectResolver.getResolver(effectToApply).apply(game, effect.getCard()));
        }

        for (EffectsToApply effect : otherEffects) {
            effect.getEffects().forEach(effectToApply -> AbstractEffectResolver.getResolver(effectToApply).apply(game, effect.getCard()));
        }
    }
}
