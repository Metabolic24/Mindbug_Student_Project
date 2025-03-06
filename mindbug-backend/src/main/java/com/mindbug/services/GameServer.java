package com.mindbug.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.mindbug.models.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.configs.Config;
import com.mindbug.dtos.PlayerBasicInfoDto;
import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessageMatchFound;
import com.mindbug.websocket.WSMessageManager;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GameServer {
    private HashMap<Long, GameSession> gameSessions = new HashMap<>();
    private Queue<Player> playerQueue = new LinkedList<>();

    private final PlayerService playerService;
    private final GameSessionFactory gameSessionFactory;
    private final WSMessageManager gameQueueWsMessageManager;

    @Autowired
    public GameServer(PlayerService playerService, GameSessionFactory gameSessionFactory, WSMessageManager gameQueueWsMessageManager) {
        this.playerService = playerService;
        this.gameSessionFactory = gameSessionFactory;
        this.gameQueueWsMessageManager = gameQueueWsMessageManager;
        // Set the channel for game queue WebSocket messages
        this.gameQueueWsMessageManager.setChannel(Config.GAME_QUEUE_WEBSOCKET);
    }

    public PlayerBasicInfoDto handleJoinGame(GameService gameService) {
        // Create the player and add to queue
        Player player = this.playerService.createPlayer(new Player("Player"));
        this.playerQueue.add(player);

        // Create game session if two players in queue
        if (this.playerQueue.size() >= 2) {
            this.createGameSession(this.playerQueue.poll(), this.playerQueue.poll(), gameService);
        }

        return new PlayerBasicInfoDto(player);
    }

    public void handleConfirmJoin(Long gameId, Long playerId) {
        GameSession gameSession = this.getGameSession(gameId);
        if (gameSession == null)
            throw new EntityNotFoundException("Game not found");
        gameSession.confirmJoin(playerId);
    }

    private void createGameSession(Player player1, Player player2, GameService gameService) {
        Game newGame = gameService.createGame(new Game());
        newGame.setPlayer1(player1);
        newGame.setPlayer2(player2);

        // Create game session
        GameSession gameSession = gameSessionFactory.createGameSession(newGame);
        gameSessions.put(newGame.getId(), gameSession);

        // Initiate the game
        gameService.initializeGame(newGame.getId(), this);
        // Send newGame websocket messages to each player
        this.gameQueueWsMessageManager.sendMessage(new WSMessageMatchFound(newGame.getId(), newGame.getPlayer1().getId()));
        this.gameQueueWsMessageManager.sendMessage(new WSMessageMatchFound(newGame.getId(), newGame.getPlayer2().getId()));
    }

    public List<PlayerBasicInfoDto> getPlayersInGame(Long gameId) {
        GameSession gameSession = this.getGameSession(gameId);
        if (gameSession == null) {
            throw new EntityNotFoundException("Game not found");
        }
        return gameSession.getPlayers().stream()
                .map(PlayerBasicInfoDto::new)
                .collect(Collectors.toList());
    }

    public GameSession getGameSession(Long id) {
        return this.gameSessions.get(id);
    }

    public List<Card> getPlayerCardsHand(Long gameId, Long playerId) {
        GameSession gameSession = this.getGameSession(gameId);
        if (gameSession == null) {
            throw new EntityNotFoundException("Game not found");
        }
        Player player = gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Player not found"));
        return player.getHand();
    }

    public List<Card> getPlayerCardsDrawPile(Long gameId, Long playerId) {
        GameSession gameSession = this.getGameSession(gameId);
        if (gameSession == null) {
            throw new EntityNotFoundException("Game not found");
        }
        Player player = gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Player not found"));
        return player.getDrawPile();
    }

}