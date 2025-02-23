package com.mindbug.services;

import org.springframework.stereotype.Service;

import com.mindbug.models.Game;

@Service
public class GameSessionValidation {

    public void validPLayer(GameSession gameSession, Long playerId) {
        Game game = gameSession.getGame();
        if (playerId != game.getPlayer1().getId() && playerId != game.getPlayer2().getId()) {
            // Cannot confrim join. Invalid player.
            throw new IllegalArgumentException("Cannot confrim join. Invalid player.");
        }
    }

    public void canConfirmJoin(GameSession gameSession, Long playerId) {
        if (gameSession.getStatus() != GameStatus.NOT_STARTED) {
            // Cannot confrim join. Game already started.
            throw new IllegalStateException("Cannot confrim join. Game already started.");
        } 

        validPLayer(gameSession, playerId);
    }
}
