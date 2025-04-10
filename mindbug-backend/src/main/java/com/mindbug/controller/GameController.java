package com.mindbug.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.mindbug.services.GameServer;
import com.mindbug.dtos.ConfirmJoinDto;
import com.mindbug.dtos.PlayerBasicInfoDto;
import com.mindbug.dtos.PlayerCardDto;
import com.mindbug.dtos.PlayerIdDto;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameServer gameServer;

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

    @PostMapping("/attack")
    public ResponseEntity<String>  attack(@RequestBody PlayerCardDto data) {
        this.gameServer.handleAttack(data.getPlayerId(), data.getGameId(), data.getSessioncardId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dont_block")
    public ResponseEntity<String>  dontBlock(@RequestBody PlayerIdDto data) {
        this.gameServer.handleDontBlock(data.getPlayerId(), data.getGameId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    public ResponseEntity<String>  block(@RequestBody PlayerCardDto data) {
       this.gameServer.handleBlock(data.getPlayerId(), data.getSessioncardId(), data.getGameId());
       return ResponseEntity.ok().build();

    }

    @PostMapping("/play_card")
    public ResponseEntity<String> playCard(@RequestBody PlayerCardDto data) {
        this.gameServer.handlePlayCard(data.getPlayerId(), data.getSessioncardId(), data.getGameId());
        return ResponseEntity.ok().build();
    }

}
