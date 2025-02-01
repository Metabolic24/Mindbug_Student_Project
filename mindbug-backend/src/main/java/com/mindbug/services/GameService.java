package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.repositories.GameRepository;

@Service
public class GameService {

    // temp. we use it until we implement multiplayer
    private Game waitingGame;
    private int call = 0;

    @Autowired
    private GameRepository gameRepository;

    public void newGame() {
        if(call == 0) {
            // First call of the service. Create player 1
            Player player1 = new Player("joueur 1");
            this.waitingGame = new Game();
            this.waitingGame.setPlayer1(player1);
        }
    }
}
