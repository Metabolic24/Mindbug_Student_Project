package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.MindbugChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility service that updates game state when a card is picked or played
 */
public class PlayCardService {

    // Not to be used
    private PlayCardService() {
        // Nothing to do
    }

    /**
     * Method executed when a player choose a card that he would like to play
     *
     * @param card the picked card
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void pickCard(CardInstance card, Game game) throws GameStateException, WebSocketException {
        System.out.println("22 Player " + game.getCurrentPlayer().getName() + " picked card " + card.getCard().getName()); //To be removed when WebSocket update will be implemented
        if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card",
                    Map.of("choice", game.getChoice()));
        }

        // Update current player hand
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.getHand().remove(card);
        currentPlayer.refillHand();

        List<Player> availableMindbuggers = getAvailableMindbuggers(game);

        if (availableMindbuggers.isEmpty()) {
            playCard(card, game);
        } else {
            game.setChoice(new MindbugChoice(card, availableMindbuggers));

            // Send update through WebSocket
            WebSocketService.sendGameEvent(WsGameEventType.CARD_PICKED  , game); //TODO Est-ce que ça vaut toujours le coup d'envoyer un événement spécifique CARD_PICKED alors que c'est un choix ? (A réfléchir)
            HistoryService.log(game, HistoryKey.PICK, card);
            System.out.println("Player " + currentPlayer.getName() + " picked card " + card.getCard().getName() + " and can be mindbugged by " + availableMindbuggers.stream().map(Player::getName).toList());
        }
    }

    /**
     * @param game the current game state
     * @return the list of player that can mindbug a card played by the current player
     */
    private static List<Player> getAvailableMindbuggers(Game game) {
        List<Player> availableMindbuggers = new ArrayList<>();
        List<Player> players = game.getPlayers();
        int currentPlayerIndex = players.indexOf(game.getCurrentPlayer());
        int playersCount = players.size();
        int currentIndex = (currentPlayerIndex + 1) % playersCount;

        while (currentIndex != currentPlayerIndex) {
            Player currentPlayer = players.get(currentIndex);
            if (currentPlayer.getMindBugs() > 0) {
                availableMindbuggers.add(currentPlayer);
            }

            currentIndex = (currentIndex + 1) % playersCount;
        }

        return availableMindbuggers;
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     *
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void playCard(CardInstance playedCard, Game game) throws GameStateException, WebSocketException {
        playCard(playedCard, null, game);
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     *
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game       the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void playCard(CardInstance playedCard, Player mindbugger, Game game) throws GameStateException, WebSocketException {
        if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card",
                    Map.of("choice", game.getChoice()));
        } else if (mindbugger != null) {
            if (mindbugger.equals(game.getCurrentPlayer())) {
                throw new GameStateException(MessageFormat.format("player {0} cannot mindbug its own card", mindbugger.getName()),
                        Map.of("mindbugger", mindbugger));
            } else if (!mindbugger.hasMindbug()) {
                throw new GameStateException(MessageFormat.format("player {0} has no mindbug left", mindbugger.getName()),
                        Map.of("mindbugger", mindbugger));
            }
        }

        managePlayedCard(playedCard, mindbugger, game);

        // Send update through WebSocket
        WebSocketService.sendGameEvent(WsGameEventType.CARD_PLAYED, game);

        HistoryKey historyKey = mindbugger != null ? HistoryKey.MINDBUG : HistoryKey.PLAY;
        HistoryService.log(game, historyKey, playedCard);

        // Resolve the effect queue so PLAY effects will be resolved then afterEffect executed
        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Update game state after a card is played
     *
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game       the current game state
     * @throws GameStateException if an error occurred while refreshing game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    protected static void managePlayedCard(CardInstance playedCard, Player mindbugger, Game game) throws GameStateException, WebSocketException {
        // Specific behavior if card has been mindbugged
        if (mindbugger != null) {
            playedCard.setOwner(mindbugger);
            mindbugger.useMindbug();
        }

        // Add card to its owner board then refresh game state
        playedCard.getOwner().getBoard().add(playedCard);
        GameStateService.refreshGameState(game);

        // Add PLAY effects (if any) if player is allowed to trigger them
        EffectQueueService.addBoardEffectsToQueue(playedCard, EffectTiming.PLAY, game.getEffectQueue());

        game.setAfterEffect(() -> {
            // Start a new turn but only changes current player if card has not been mindbugged
            GameStateService.newTurn(game, mindbugger != null);
        });
    }
}
