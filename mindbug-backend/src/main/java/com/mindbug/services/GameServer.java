package com.mindbug.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.configs.Config;
import com.mindbug.dtos.PlayerBasicInfoDto;
import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.websocket.WSMessageManager;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GameServer {
    private HashMap<Long, GameSession> gameSessions = new HashMap<>();
    private Queue<Player> playerQueue = new LinkedList<>();

    @Autowired
    private PlayerService playerservice;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionFactory gameSessionFactory;

    private WSMessageManager gameQueueWsMessageManager;

    public GameServer(WSMessageManager gameQueueWsMessageManager) {
        this.gameQueueWsMessageManager = gameQueueWsMessageManager;
        // Set the channel for game queue WebSocket messages
        this.gameQueueWsMessageManager.setChannel(Config.GAME_QUEUE_WEBSOCKET);
    }

    public PlayerBasicInfoDto handleJoinGame() {
        // Create the player and add to queue
        Player player =  this.playerservice.createPlayer(new Player("Player"));
        this.playerQueue.add(player);

        // Create game session if two player in queue
        if (this.playerQueue.size() >= 2) {
            this.createGameSession(this.playerQueue.poll(), this.playerQueue.poll());
        }

        return (new PlayerBasicInfoDto(player));
    }

    public void handleConfirmJoin(Long gameId, Long playerId) {
        GameSession gameSession = this.getGameSession(gameId);
        if (gameSession == null)
            throw new EntityNotFoundException("Game not found");
        gameSession.confirmJoin(playerId);
    }

    private void createGameSession(Player player1, Player player2) {
        Game newGame = this.gameService.createGame(new Game());
        newGame.setPlayer1(player1);
        newGame.setPlayer2(player2);

        // Create game session
        GameSession gameSession = gameSessionFactory.createGameSession(newGame);
        gameSessions.put(newGame.getId(), gameSession);

        // Send newGame websocket messages to each player
        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", newGame.getId());
        
        // First player message
        data.put("playerId", newGame.getPlayer1().getId());


        this.gameQueueWsMessageManager.sendMessage("MATCH_FOUND", data);

        // Second player message
        data.put("playerId", newGame.getPlayer2().getId());
        this.gameQueueWsMessageManager.sendMessage("MATCH_FOUND", data);

    }

    private GameSession getGameSession(Long id) {
        return this.gameSessions.get(id);
    }

}
