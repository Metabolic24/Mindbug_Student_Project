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

    public PlayerBasicInfoDto(Player player) {
        this.playerId = player.getId();
        this.nickname = player.getNickname();
    }
    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
