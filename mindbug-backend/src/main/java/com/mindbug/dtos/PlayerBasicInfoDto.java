package com.mindbug.dtos;

import com.mindbug.models.Player;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerBasicInfoDto {
    @NotNull
    private Long playerId;

    @NotNull
    private String nickname;

    @NotNull
    private Long gameId;

    public PlayerBasicInfoDto(Player player) {
        this.playerId = player.getId();
        this.nickname = player.getNickname();
    }
}
