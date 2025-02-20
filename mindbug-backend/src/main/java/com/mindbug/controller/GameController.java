package com.mindbug.controller;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
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

    @Autowired
    private GameSession gameSession;

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
            //gameSession.distributionCard();
            return ResponseEntity.ok("Card distribution test successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Card distribution test failed: " + e.getMessage());
        }
    }

    @PostMapping("/select-card")
    public ResponseEntity<String> selectCard(@RequestBody Map<String, String> data) {
        Integer cardIndex = Integer.parseInt(data.get("cardIndex"));
        String playerNickname = data.get("playerNickname");
        try {
            gameSession.selectCard(playerNickname, cardIndex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Card selection failed: " + e.getMessage());
        }
        return ResponseEntity.ok("Card selected successfully");
    }

    @PostMapping("/play-card")
    public ResponseEntity<String> playCard(@RequestBody Map<String, String> data) {
        String playerNickname = data.get("playerNickname");
        try {
            gameSession.playCard(playerNickname);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Card play failed: " + e.getMessage());
        }
        return ResponseEntity.ok("Card played successfully");
    }
    @GetMapping("player-hand-card")
    public ResponseEntity<String> getPlayerHandCard(@RequestParam String playerNickname) {
        try {
            return ResponseEntity.ok(gameSession.getPlayerHandCard(playerNickname));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get player hand card: " + e.getMessage());
        }
    }
}