package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.service.EffectQueueService;
import org.metacorp.mindbug.service.WebSocketService;

import java.util.HashMap;
import java.util.Map;

import static org.metacorp.mindbug.service.game.GameStateService.refreshGameState;

/**
 * Service to manage choices
 */
public class ChoiceService {

    /**
     * Resolve the current game choice using the input data
     * @param data the data to use to resolve choice
     * @param game the game to update
     * @param <T> the input data type
     * @throws GameStateException if an error occured during choice resolution
     */
    public static <T> void resolveChoice(T data, Game game) throws GameStateException {
        IChoice<?> choice = game.getChoice();
        if (choice == null) {
            throw new GameStateException("no choice to be resolved", Map.of("data", data));
        } else if (data == null && choice.getType() != ChoiceType.HUNTER) {
            throw new GameStateException("invalid data for choice resolution", Map.of("choice", choice));
        }

        try {
            ((IChoice<T>) choice).resolve(data, game);
        } catch (ClassCastException e) {
            Map<String, Object> errorData = new HashMap<>(Map.of("choice", choice));
            if (data != null) {
                errorData.put("data", data);
            }

            throw new GameStateException("invalid choice resolution", errorData);
        }

        refreshGameState(game);

        if (game.getChoice() == null) {
            EffectQueueService.resolveEffectQueue(choice.getType() == ChoiceType.SIMULTANEOUS, game);
        } else {
            WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
        }
    }

    /**
     * Constructor
     */
    private ChoiceService() {
        // Not to be used
    }
}
