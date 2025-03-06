package com.mindbug.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerCardDto {

    @NotNull
    private Long playerId;

    @NotNull
    private Long gameId;

    @NotNull
    private Long sessioncardId;

    @NotEmpty 
    private String cardName;

    public PlayerCardDto(@NotEmpty Long playerId, @NotEmpty Long gameId, @NotEmpty Long cardId, @NotEmpty String cardName) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.sessioncardId = cardId;
        this.cardName = cardName;
    }
}
