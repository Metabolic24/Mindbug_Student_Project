package com.mindbug.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.mindbug.services.GameServer;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameServer gameService;

    @PostMapping("/join_game")
    public ResponseEntity<Object> joinGame() {
        Object data = this.gameService.createGameSession();
        return ResponseEntity.ok(data);
    }


}
