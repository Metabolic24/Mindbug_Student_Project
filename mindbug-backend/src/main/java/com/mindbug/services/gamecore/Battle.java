package com.mindbug.services.gamecore;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope("prototype")
@Getter @Setter
public class Battle {
    private PlayerCard attacking;
    private PlayerCard blocking;

    public void attack(Player attackingPlayer, GameSessionCard attackingSessionCard) {
        this.attacking = new PlayerCard(attackingPlayer, attackingSessionCard);
    }

    public void dontBlock(Player blockingPlayer) {
        this.blocking = new PlayerCard(blockingPlayer);
    }
    
}
