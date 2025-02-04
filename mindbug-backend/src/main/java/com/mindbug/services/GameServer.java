package com.mindbug.services;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.models.Player;

@Service
public class GameServer {

    // temp. we use it until we implement multiplayer
    private Game waitingNewGame;
    private int call = 0;
    private HashMap <Long, GameSession> gameSessions = new HashMap<>();

    @Autowired
    private PlayerService playerservice;

    public Player createGameSession() {
        if(call == 0) {
            // First call of the service. Create player 1
            Player player1 = this.playerservice.createPlayer(new Player("Player 1"));
            this.waitingNewGame = new Game();
            this.waitingNewGame.setPlayer1(player1);
            call++;

            return player1;
            
        } else {
            // Second call. Create Player 2 and return game state
            Player player2 = this.playerservice.createPlayer(new Player("Player 2"));
            this.waitingNewGame.setPlayer2(player2);

            // Create game session
            GameSession gameSession = new GameSession(waitingNewGame); // websocket in gameSession constructor
            gameSessions.put(waitingNewGame.getId(), gameSession);

            // Reset
            this.resetMacthmaking();

            return player2;
        }
    }

    public void resetMacthmaking() {
        this.waitingNewGame = null;
        this.call = 0;
    }
}
