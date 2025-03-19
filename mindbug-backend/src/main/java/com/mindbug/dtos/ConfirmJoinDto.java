package com.mindbug.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmJoinDto {

    @NotNull
    private Long playerId;

    @NotNull
    private Long gameId;
}
