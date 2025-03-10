package com.mindbug.services.gamecore;

import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.models.Player;

@Service
public class GameSessionValidation {

    public void validPlayer(GameSession gameSession, Long playerId) {
        Game game = gameSession.getGame();
        if (playerId != game.getPlayer1().getId() && playerId != game.getPlayer2().getId()) {
            // Cannot confrim join. Invalid player.
            throw new IllegalArgumentException("Cannot confrim join. Invalid player.");
        }
    }

    public void canConfirmJoin(GameSession gameSession, Long playerId) {
        validPlayer(gameSession, playerId);
    }

    public void canAttack(GameSession gameSession, Long playerId, Long cardId) {
        // Check valid player
        validPlayer(gameSession, playerId);

        // Check if player have a card
        Player player = gameSession.getPlayer(playerId);
        boolean haveCard = player.getHand().stream().anyMatch(sessionCard -> sessionCard.getId() == cardId);
        if (!haveCard) {
            throw new IllegalStateException("Player " + playerId + " does not have card " + cardId + ".");
        }
    }
}
