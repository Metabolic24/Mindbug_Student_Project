package com.mindbug.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerCardDto {

    @NotEmpty
    private Long playerId;

    @NotEmpty
    private Long gameId;

    @NotEmpty
    private Long cardId;

    public PlayerCardDto(@NotEmpty Long playerId, @NotEmpty Long gameId, @NotEmpty Long cardId) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.cardId = cardId;
    }
}
