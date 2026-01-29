package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.metacorp.mindbug.service.game.CardService.getPassiveEffects;

public class GameStateService {

    public static void refreshGameState(Game game) {
        refreshGameState(game, false);
    }

    public static void refreshGameState(Game game, boolean newTurn) {
        List<EffectsToApply> passiveEffects = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            player.refresh(newTurn);

            passiveEffects.addAll(getPassiveEffects(player.getBoard(), false));
            passiveEffects.addAll(getPassiveEffects(player.getDiscardPile(), true));
        }

        // Sort effects by priority
        passiveEffects.sort(Comparator.comparingInt(effect -> ((GenericEffect) effect.getEffects().getFirst()).getPriority()));

        // Apply effects
        for (EffectsToApply effect : passiveEffects) {
            EffectResolver<?> effectResolver = EffectResolver.getResolver(effect.getEffects().getFirst());
            effectResolver.apply(game, effect.getCard(), effect.getTiming());
        }
    }

    public static void newTurn(Game game) {
        newTurn(game, false);
    }

    public static void newTurn(Game game, boolean mindbug) {
        if (!mindbug) {
            game.setNextPlayer();
        }

        refreshGameState(game, true);

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getHand().isEmpty() && currentPlayer.getBoard().stream().noneMatch(CardInstance::isAbleToAttack)) {
            endGame(currentPlayer, game);
        } else {
            WebSocketService.sendGameEvent(WsGameEventType.NEW_TURN, game);
        }
    }

    public static void lifePointLost(Player player, Game game) {
        WebSocketService.sendGameEvent(WsGameEventType.LP_DOWN, game);

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

    public static void endGame(Player loser, Game game) {
        if(game.typeGameMode() == 1){
            Player winner = loser.getOpponent(game.getPlayers()).get(0);
            System.out.println("\n<<<<< GAME OVER >>>>>");
            System.out.printf("%s wins ; %s loses\n", winner.getName(), loser.getName());

            game.setWinner(List.of(winner));

            WebSocketService.sendGameEvent(WsGameEventType.FINISHED, game);
        } 
        else if(game.typeGameMode() == 2){
            List<Player> winners = loser.getOpponent(game.getPlayers());
            Player loserAllie = loser.getAllie(game.getPlayers());

            System.out.println("\n<<<<< GAME OVER >>>>>");
            System.out.printf("%s & %s win ; %s & %s lose\n", winners.get(0).getName(), winners.get(1).getName(), loser.getName(), loserAllie.getName());

            game.setWinner(winners);

            // TODO
            // Add the websocket game event
        }
    }

    /**
     * Constructor
     */
    private GameStateService() {
        // Not to be used
    }
}
