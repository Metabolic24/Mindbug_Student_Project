// GameController.java
package com.mindbug.controller;

import com.mindbug.models.Card;
import com.mindbug.services.GameService;
import com.mindbug.services.GameServer;
import com.mindbug.dtos.ConfirmJoinDto;
import com.mindbug.dtos.PlayerBasicInfoDto;
import com.mindbug.services.GameSession;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameServer gameServer;
    private final GameService gameService;

    @Autowired
    public GameController(GameServer gameServer, GameService gameService) {
        this.gameServer = gameServer;
        this.gameService = gameService;
    }

    @PostMapping(value = "/join_game", produces = "application/json")
    public ResponseEntity<PlayerBasicInfoDto> joinGame() {
        PlayerBasicInfoDto playerInfo = gameServer.handleJoinGame(gameService);
        return ResponseEntity.ok(playerInfo);
    }

    @PostMapping("/confirm_join")
    public ResponseEntity<String> confirmJoin(@RequestBody ConfirmJoinDto data) {
        this.gameServer.handleConfirmJoin(data.getGameId(), data.getPlayerId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/players/{gameId}")
    public ResponseEntity<List<PlayerBasicInfoDto>> getPlayersInGame(@PathVariable Long gameId) {
        List<PlayerBasicInfoDto> players = gameServer.getPlayersInGame(gameId);
        return ResponseEntity.ok(players);
    }

    @PostMapping("/{gameId}/initialize-game")
    public ResponseEntity<Void> initializeGame(@PathVariable Long gameId) {
        gameService.initializeGame(gameId, gameServer);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{gameId}/player/{playerId}/Hand")
    public ResponseEntity<List<String>> getPlayerCardsHand(@PathVariable Long gameId, @PathVariable Long playerId) {
        List<String> cardNames = gameServer.getPlayerCardsHand(gameId, playerId).stream()
                .map(Card::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cardNames);
    }

    @GetMapping("/{gameId}/player/{playerId}/DrawPile")
    public ResponseEntity<List<String>> getPlayerCardsDrawPile(@PathVariable Long gameId, @PathVariable Long playerId) {
        List<String> cardNames = gameServer.getPlayerCardsDrawPile(gameId, playerId).stream()
                .map(Card::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cardNames);
    }

    @PostMapping("/{gameId}/player/{playerId}/selectCard")
    public ResponseEntity<String> selectCard(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody String cardName) {
        try {
            GameSession gameSession = gameServer.getGameSession(gameId);
            gameSession.selectCard(gameSession.getPlayers().stream()
                    .filter(p -> p.getId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Player not found")), cardName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameId}/player/{playerId}/unselectCard")
    public ResponseEntity<Void> unselectCard(@PathVariable Long gameId, @PathVariable Long playerId) {
        GameSession gameSession = gameServer.getGameSession(gameId);
        gameSession.unselectCard(gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Player not found")));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/player/{playerId}/playCard")
    public ResponseEntity<Void> playCard(@PathVariable Long gameId, @PathVariable Long playerId) {
        GameSession gameSession = gameServer.getGameSession(gameId);
        gameSession.playCard(gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Player not found")));
        return ResponseEntity.ok().build();
    }

}