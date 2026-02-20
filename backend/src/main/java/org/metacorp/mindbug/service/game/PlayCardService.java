package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

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
     */
    public static void pickCard(CardInstance card, Game game) throws GameStateException {
        if (game.getPlayedCard() != null) {
            throw new GameStateException("a card has already been picked", Map.of("playedCard", game.getPlayedCard()));
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card",
                    Map.of("choice", game.getChoice()));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before picking a new card",
                    Map.of("attackingCard", game.getAttackingCard()));
        }

        // Update current player hand
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.getHand().remove(card);
        currentPlayer.refillHand();

        // Update game state
        game.setPlayedCard(card);

        // Usage of mindbug depending on the game mode
        // 1v1 game mode
        if(game.typeGameMode() == 1){
            Player opponent = game.getOpponent().getFirst();
            if (opponent.getMindBugs() == 0) {
                playCard(game);
                System.out.println("L'opposant n'a plus de Mindbug pour voler la carte");
            } else {

                askMindbugChoice(0, List.of(opponent), card, game);

                // Send update through WebSocket
                WebSocketService.sendGameEvent(WsGameEventType.CARD_PICKED, game);
                HistoryService.log(game, HistoryKey.PICK, card);
            }
        }
        // 2v2 game mode
        if(game.typeGameMode() == 2){
            Player nextOpponent = game.getOpponent().getFirst();
            Player previousOpponent = game.getOpponent().get(1);
            Player allie = game.getAllie();
            List<Player> mindbugQueue = new ArrayList<>();

            if (nextOpponent.hasMindbug()) {
                mindbugQueue.add(nextOpponent);
            }
            if (allie.hasMindbug()) {
                mindbugQueue.add(allie);
            }
            if (previousOpponent.hasMindbug()) {
                mindbugQueue.add(previousOpponent);
            }
    
            if(mindbugQueue.isEmpty()){
                playCard(game);
            } else{
                askMindbugChoice(0, mindbugQueue, card, game);

                // TODO
                // Send update through WebSocket
            }
        }
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     *
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     */
    public static void playCard(Game game) throws GameStateException {
        playCard(null, game);
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     *
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game       the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     */
    public static void playCard(Player mindbugger, Game game) throws GameStateException {
        if (game.getPlayedCard() == null) {
            throw new GameStateException("no card has been picked");
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card",
                    Map.of("choice", game.getChoice()));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before picking a new card",
                    Map.of("attackingCard", game.getAttackingCard()));
        } else if (mindbugger != null) {
            if (mindbugger.equals(game.getCurrentPlayer())) {
                throw new GameStateException(MessageFormat.format("player {0} cannot mindbug its own card", mindbugger.getName()),
                        Map.of("mindbugger", mindbugger));
            } else if (!mindbugger.hasMindbug()) {
                throw new GameStateException(MessageFormat.format("player {0} has no mindbug left", mindbugger.getName()),
                        Map.of("mindbugger", mindbugger));
            }
        }

        managePlayedCard(mindbugger, game);

        // Send update through WebSocket
        WebSocketService.sendGameEvent(WsGameEventType.CARD_PLAYED, game);

        HistoryKey historyKey = mindbugger != null ? HistoryKey.MINDBUG : HistoryKey.PLAY;
        HistoryService.log(game, historyKey, game.getPlayedCard());

        // Resolve the effect queue so PLAY effects will be resolved then afterEffect executed
        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Update game state after a card is played
     *
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game       the current game state
     */
    protected static void managePlayedCard(Player mindbugger, Game game) {
        CardInstance playedCard = game.getPlayedCard();

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
            // Reset the played card value
            game.setPlayedCard(null);

            // Start a new turn but only changes current player if card has not been mindbugged
            GameStateService.newTurn(game, mindbugger != null);
        });
    }

    /**
     * Asks at players who have Mindbug(s) if they want to use Mindbug
     * 
     * @param index the index of the first player at ask
     * @param candidates list of the players who can use Mindbug
     * @param card the card who can be Mindbug
     * @param game the current game state
     */
    private static void askMindbugChoice(int index, List<Player> candidates, CardInstance card, Game game) {
        if (index >= candidates.size()) {
            try {
                playCard(game);
            } catch (GameStateException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        Player current = candidates.get(index);

        ResolvableEffect<Boolean> effect = (g, useMindbug) -> {
            try {
                if (useMindbug) {
                    PlayCardService.playCard(current, g);
                } else {
                    askMindbugChoice(index + 1, candidates, card, g);
                }
            } catch (GameStateException e) {
                throw new RuntimeException(e);
            }
        };

        BooleanChoice mindbugChoice = new BooleanChoice(
            current,
            card,
            effect
        );

        mindbugChoice.setPrompt(
            current.getName() +
            " : voulez-vous utiliser un Mindbug pour voler cette carte ? (O/N)"
        );

        game.setChoice(mindbugChoice);
    }

}
