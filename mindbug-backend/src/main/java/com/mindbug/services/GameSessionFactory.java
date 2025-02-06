package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.websocket.WSMessageManager;

@Service
public class GameSessionFactory {
    @Autowired
    private WSMessageManager wsMessageManager;
    
    public GameSession createGameSession(Game game) {
        GameSession gameSession = new GameSession(wsMessageManager);
        gameSession.initialize(game);
        return gameSession;
    }
}
