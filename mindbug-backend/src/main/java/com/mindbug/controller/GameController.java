package com.mindbug.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.mindbug.services.GameService;

import com.mindbug.models.Game;
@RestController
@RequestMapping("/api/game")
public class GameController {
    
    @Autowired
    private GameService gameService;

    @GetMapping("/join_game")
    public ResponseEntity<Game> joinGame() {
        Game game = this.gameService.newGame();
        return ResponseEntity.ok(game);
    }
}
