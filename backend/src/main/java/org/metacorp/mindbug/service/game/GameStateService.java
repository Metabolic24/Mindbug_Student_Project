package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.metacorp.mindbug.service.game.CardService.getPassiveEffects;

public class GameStateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateService.class);

    /**
     * Refresh the game state
     *
     * @param game the current game state
     * @throws GameStateException if an error occurred while refreshing game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void refreshGameState(Game game) throws GameStateException, WebSocketException {
        refreshGameState(game, false);
    }

    /**
     * Refresh the game state
     *
     * @param game    the current game state
     * @param newTurn true if it has been triggered at the end of a player's turn
     * @throws GameStateException if an error occurred while refreshing game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void refreshGameState(Game game, boolean newTurn) throws GameStateException, WebSocketException {
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

    /**
     * Starts a new turn
     *
     * @param game the current game state
     * @throws GameStateException if an error occurred while refreshing game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void newTurn(Game game) throws GameStateException, WebSocketException {
        newTurn(game, false);
    }

    /**
     * Starts a new turn
     *
     * @param game    the current game state
     * @param mindbug indicates whether the last turn was ended by a mindbug use
     * @throws GameStateException if an error occurred while refreshing game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void newTurn(Game game, boolean mindbug) throws GameStateException, WebSocketException {
        if (!mindbug) {
            game.setCurrentPlayer(game.getOpponent());
        }

        refreshGameState(game, true);

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getHand().isEmpty() && currentPlayer.getBoard().stream().noneMatch(CardInstance::isAbleToAttack)) {
            endGame(currentPlayer, game);
        } else {
            WebSocketService.sendGameEvent(WsGameEventType.NEW_TURN, game);
        }
    }

    /**
     * Method triggered when a player loses at least one life point
     *
     * @param player the player that lost life point(s)
     * @param game   the current game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void lifePointLost(Player player, Game game) throws WebSocketException {
        WebSocketService.sendGameEvent(WsGameEventType.LP_DOWN, game);
        HistoryService.logLifeUpdate(game, player);

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

    /**
     * Update the game state after game finished
     *
     * @param loser the losing player
     * @param game  the current game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void endGame(Player loser, Game game) throws WebSocketException {
        Player winner = loser.getOpponent(game.getPlayers());
        LOGGER.info("Game over : {} wins ; {} loses.", winner.getName(), loser.getName());

        game.setWinner(winner);

        WebSocketService.sendGameEvent(WsGameEventType.FINISHED, game);
        HistoryService.saveHistory(game);
    }

    /**
     * Constructor
     */
    private GameStateService() {
        // Not to be used
    }
}
