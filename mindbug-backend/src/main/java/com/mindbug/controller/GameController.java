package com.mindbug.controller;

import com.mindbug.models.Game;
import com.mindbug.services.GameSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.mindbug.services.GameServer;
import com.mindbug.dtos.ConfirmJoinDto;
import com.mindbug.models.Player;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameServer gameServer;

    @PostMapping("/join_game")
    public ResponseEntity<Player> joinGame() {
        Player data = this.gameServer.handleJoinGame();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/confirm_join")
    public ResponseEntity<String> confirmJoin(@RequestBody ConfirmJoinDto data) {
        this.gameServer.handleConfirmJoin(data.getGameId(), data.getPlayerId());
        return ResponseEntity.ok().build();
    }

    @Autowired
    private GameSession gameSession;

    @PostMapping("/distribute-cards")
    public ResponseEntity<String> distributeCards(@RequestBody Game game) {
        if (game == null) {
            return ResponseEntity.badRequest().body("Game object is missing");
        }
        gameSession.initialize(game);
        return ResponseEntity.ok("Cards distributed successfully");
    }

    @GetMapping("/test-distribution")
    public ResponseEntity<String> testDistribution() {
        try {
            Game game = new Game();
            // Initialize the game object with necessary data
            game.setPlayer1(new Player("Player 1"));
            game.setPlayer2(new Player("Player 2"));
            gameSession.initialize(game);
            gameSession.distributionCard();
            return ResponseEntity.ok("Card distribution test successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Card distribution test failed: " + e.getMessage());
        }
    }
}
