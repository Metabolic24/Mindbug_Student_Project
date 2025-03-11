package org.metacorp.mindbug.service;

import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

import java.util.*;

@Service
public class GameService {

    private final Map<UUID, Game> games = new HashMap<>();

    public Game createGame() {
        Game game = StartService.newGame("player1", "player2");
        games.put(game.getUuid(), game);

        return game;
    }

    public Game findById(UUID uuid) {
        return games.get(uuid);
    }

    public static <T> void resolveChoice(T data, Game game) throws GameStateException {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            throw new GameStateException("no choice to be resolved", Map.of("data", data));
        } else if (data == null) {
            throw new GameStateException("invalid data for choice resolution", Map.of("choice", choice));
        }

        try {
            ((IChoice<T>) choice).resolve(data, game);

            GameService.refreshGameState(game);
            EffectQueueService.resolveEffectQueue(choice.getType() == ChoiceType.SIMULTANEOUS, game);
        } catch (ClassCastException e) {
            throw new GameStateException("invalid choice resolution", e, Map.of("choice", choice, "data", data));
        }
    }

    public static void endGame(Player loser, Game game) {
        Player winner = loser.getOpponent(game.getPlayers());
        System.out.printf("%s wins ; %s loses\n", winner.getName(), loser.getName());

        game.setFinished(true);
    }

    public static void lifePointLost(Player player, Game game) {
        if (player.getTeam().getLifePoints() <= 0) {
            endGame(player, game);
            return;
        }

        for (CardInstance card : player.getBoard()) {
            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.LIFE_LOST, game.getEffectQueue());
        }

        for (CardInstance card : player.getDiscardPile()) {
            EffectQueueService.addDiscardEffectsToQueue(card, EffectTiming.LIFE_LOST, game.getEffectQueue());
        }
    }

    public static void defeatCard(CardInstance card, EffectQueue effectQueue) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            card.getOwner().addCardToDiscardPile(card);
            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.DEFEATED, effectQueue);
        }
    }

    public static void refreshGameState(Game game) {
        List<EffectsToApply> passiveEffects = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            player.refresh();

            passiveEffects.addAll(getPassiveEffects(player.getBoard(), false));
            passiveEffects.addAll(getPassiveEffects(player.getDiscardPile(), true));
        }

        // Sort effects by priority
        passiveEffects.sort(Comparator.comparingInt(o -> o.getEffects().getFirst().getPriority()));

        // Apply effects
        for (EffectsToApply effect : passiveEffects) {
            GenericEffectResolver<?> effectResolver = GenericEffectResolver.getResolver(effect.getEffects().getFirst());
            effectResolver.apply(game, effect.getCard());
        }
    }

    private static List<EffectsToApply> getPassiveEffects(List<CardInstance> cards, boolean inDiscard) {
        EffectTiming timing = inDiscard ? EffectTiming.DISCARD : EffectTiming.PASSIVE;

        List<EffectsToApply> passiveEffects = new ArrayList<>();
        cards.forEach(card -> passiveEffects.addAll(
                card.getEffects(timing).stream()
                .map(cardEffect -> new EffectsToApply(Collections.singletonList(cardEffect), card))
                .toList())
        );

        return passiveEffects;
    }
}
