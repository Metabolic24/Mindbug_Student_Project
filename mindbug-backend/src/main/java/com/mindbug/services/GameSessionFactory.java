package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.websocket.WSMessageManager;

@Service
public class GameSessionFactory {
    
    @Autowired
    private  ApplicationContext applicationContext;
    
    public GameSession createGameSession(Game game) {
        GameSession gameSession = this.applicationContext.getBean(GameSession.class, game, 
        this.applicationContext.getBean(WSMessageManager.class));
        return gameSession;
    }
}
