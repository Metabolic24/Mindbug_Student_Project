package com.mindbug.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
import org.springframework.context.ApplicationContext;

@Service
public class GameServer {

    // temp. we use it until we implement multiplayer
    private Game waitingNewGame;
    private int call = 0;
    private HashMap <Long, GameSession> gameSessions = new HashMap<>();

    @Autowired
    private PlayerService playerservice;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionFactory gameSessionFactory;

    public Object createGameSession() {
        Map<String, Object> res = new HashMap<String,Object>();

        if(call == 0) {
            // First call of the service. Create player 1 and the game
            Player player1 = this.playerservice.createPlayer(new Player("Player 1"));
            this.waitingNewGame = this.gameService.createGame(new Game());
            this.waitingNewGame.setPlayer1(player1);
            call++;

            res.put("player", player1);
            res.put("gameState", this.waitingNewGame);
            res.put("websocketChannel", "topic/game/"+ this.waitingNewGame.getId());
            
        } else {
            // Second call. Create Player 2 and return game state
            Player player2 = this.playerservice.createPlayer(new Player("Player 2"));
            this.waitingNewGame.setPlayer2(player2);

            // Create game session
            GameSession gameSession = gameSessionFactory.createGameSession(waitingNewGame);
            gameSessions.put(waitingNewGame.getId(), gameSession);

            res.put("player", player2);
            res.put("gameState", this.waitingNewGame);
            res.put("websocketChannel", "topic/game/"+ this.waitingNewGame.getId());

            // Reset
            this.resetMacthmaking();

            
        }

        return res;
    }

    public void resetMacthmaking() {
        this.waitingNewGame = null;
        this.call = 0;
    }
}
