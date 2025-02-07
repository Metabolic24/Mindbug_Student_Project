package com.mindbug.controller;

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

}
