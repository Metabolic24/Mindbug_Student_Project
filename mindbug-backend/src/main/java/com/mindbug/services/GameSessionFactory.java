package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.services.gamecore.GameSession;
import com.mindbug.services.gamecore.GameSessionValidation;
import com.mindbug.websocket.WSMessageManager;

@Service
public class GameSessionFactory {
    
    @Autowired
    private  ApplicationContext applicationContext;

    public GameSession createGameSession(Game game) {
        GameSession gameSession = this.applicationContext.getBean(GameSession.class, game, 
        this.applicationContext.getBean(WSMessageManager.class),
        this.applicationContext.getBean(GameSessionValidation.class), this.applicationContext,
        this.applicationContext.getBean(PlayerService.class), this.applicationContext.getBean(GameService.class));
        return gameSession;
    }
}
