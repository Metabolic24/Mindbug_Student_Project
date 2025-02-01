package com.mindbug.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.repositories.GameRepository;

@Service
public class GameService {

    // temp. we use it until we implement multiplayer
    private Game waitingNewGame;
    private int call = 0;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerservice;

    // TODO must return void. Resutl are sent thru websockets
    public Game newGame() {
        if(call == 0) {
            // First call of the service. Create player 1
            Player player1 = this.playerservice.createPlayer(new Player("Player 1"));
            this.waitingNewGame = new Game();
            this.waitingNewGame.setPlayer1(player1);
            call++;

            
            this.waitingNewGame = this.gameRepository.save(this.waitingNewGame); // store to db
            return this.waitingNewGame;
        } else {
            // Second call. Create Player 2 and return game state
            Player player2 = this.playerservice.createPlayer(new Player("Player 2"));;
            this.waitingNewGame.setPlayer2(player2);

            Game res = this.gameRepository.save(this.waitingNewGame); // save update in db

            // Reset
            this.waitingNewGame = null;
            this.call = 0;

            // Return game state
            return res;
        }
    }
}
