package com.mindbug.services.gamecore;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessagAskBlock;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope("prototype")
@Getter @Setter
public class Battle {
    private PlayerCard attacking;
    private PlayerCard blocking;

    public void attack(GameSession gameSessionContext, Player attackingPlayer, GameSessionCard attackingSessionCard) {
        this.attacking = new PlayerCard(attackingPlayer, attackingSessionCard);

        gameSessionContext.getGameWsMessageManager().sendMessage(
            new WSMessagAskBlock(
                gameSessionContext.getOpponent().getId(),
                gameSessionContext.getGame()
            )
        );
    }

    public void dontBlock(GameSession gameSessionContext, Player blockingPlayer) {
        this.blocking = new PlayerCard(blockingPlayer);
    }
    
}
