package com.mindbug.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.repositories.GameRepository;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public Game createGame(Game game) {
        return this.gameRepository.save(game);
    }

    public void initializeGame(Long gameId, GameServer gameServer) {
        GameSession gameSession = gameServer.getGameSession(gameId);
        if (gameSession == null) {
            throw new EntityNotFoundException("Game not found");
        }
        gameSession.initializeGame();
    }
}
