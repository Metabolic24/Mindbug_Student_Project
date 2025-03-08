package com.mindbug.controller;

import com.mindbug.services.GameService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.mindbug.services.GameServer;
import com.mindbug.dtos.ConfirmJoinDto;
import com.mindbug.dtos.PlayerBasicInfoDto;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameServer gameServer;

    @Autowired
    private GameService gameService;

    @Autowired
    public GameController(GameServer gameServer, GameService gameService) {
        this.gameServer = gameServer;
        this.gameService = gameService;
    }

    @PostMapping("/join_game")
    public ResponseEntity<PlayerBasicInfoDto> joinGame() {
        PlayerBasicInfoDto data = this.gameServer.handleJoinGame();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/confirm_join")
    public ResponseEntity<String> confirmJoin(@RequestBody ConfirmJoinDto data) {
        this.gameServer.handleConfirmJoin(data.getGameId(), data.getPlayerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/initialize-game")
    public ResponseEntity<Void> initializeGame(@PathVariable Long gameId) {
        gameService.initializeGame(gameId, gameServer);
        return ResponseEntity.ok().build();
    }

}
