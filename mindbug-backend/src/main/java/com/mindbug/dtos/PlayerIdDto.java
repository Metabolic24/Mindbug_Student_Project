package com.mindbug.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerIdDto {

    @NotNull
    private Long playerId;

    @NotNull
    private Long gameId;
}
