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
import com.mindbug.services.gamecore.GameSession;
import com.mindbug.services.wsmessages.WSMessageMatchFound;
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

    private void createGameSession(Player player1, Player player2) {
        Game newGame = this.gameService.createGame(new Game());
        newGame.setPlayer1(player1);
        newGame.setPlayer2(player2);

        GameSession gameSession = gameSessionFactory.createGameSession(newGame);
        gameSessions.put(newGame.getId(), gameSession);

        // Send newGame websocket messages to each player
        this.gameQueueWsMessageManager.sendMessage(new WSMessageMatchFound(newGame.getId(), newGame.getPlayer1().getId()));

        // Second player message
        this.gameQueueWsMessageManager.sendMessage(new WSMessageMatchFound(newGame.getId(), newGame.getPlayer2().getId()));

    }

    private GameSession getGameSession(Long id) {
        GameSession gameSession = this.gameSessions.get(id);

        if (gameSession == null)
            throw new EntityNotFoundException("Game not found");

        return gameSession;
    }

    public void removeGameSession(Long id) {
        GameSession removed = this.gameSessions.remove(id);

        if(removed == null)
            throw new EntityNotFoundException("Unable to remove game from gamesession. Game not found");
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
        gameSession.confirmJoin(playerId);
    }

    public void handleAttack(Long playerId, Long gameId, Long sessionCardId) {
        GameSession gameSession = this.getGameSession(gameId);
        gameSession.attack(playerId, sessionCardId);
    }


    public void handleDontBlock(Long playerId, Long gameId) {
        GameSession gameSession = this.getGameSession(gameId);
        gameSession.dontBlock(playerId);
    }

    public void handleBlock(Long playerId, Long sessionCardId, Long gameId) {
        GameSession gameSession = this.getGameSession(gameId);
        gameSession.block(playerId, sessionCardId);
    }

    public void handlePlayCard(Long playerId, Long sessionCardId, Long gameId) {
        GameSession gameSession = this.getGameSession(gameId);
        gameSession.playCard(playerId, sessionCardId);
    }

}
