package com.mindbug.dtos;

import jakarta.validation.constraints.NotNull;

public class ConfirmJoinDto {

    @NotNull
    private Long playerId;

    @NotNull
    private Long gameId;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}