package com.mindbug.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.mindbug.services.GameServer;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
@RestController
@RequestMapping("/api/game")
public class GameController {
    
    @Autowired
    private GameServer gameService;

    @PostMapping("/join_game")
    public ResponseEntity<Player> joinGame() {
        Player player = this.gameService.createGameSession();
        return ResponseEntity.ok(player);
    }
}
