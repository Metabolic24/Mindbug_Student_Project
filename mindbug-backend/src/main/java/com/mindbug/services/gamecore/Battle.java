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

    Battle() {
    }

    public void attack(GameSession gameSessionContext, Player attackingPlayer, GameSessionCard attackingSessionCard) {
        this.attacking = new PlayerCard(attackingPlayer, attackingSessionCard);

        gameSessionContext.getGameWsMessageManager().sendMessage(
            new WSMessagAskBlock(
                gameSessionContext.getOpponent().getId(),
                gameSessionContext.getGame()
            )
        );

        gameSessionContext.sendWSMsgAttacked(attackingPlayer.getId(), attackingSessionCard.getId());
    }

    public void dontBlock(GameSession gameSessionContext, Player blockingPlayer) {
        this.blocking = new PlayerCard(blockingPlayer);

        gameSessionContext.sendWSMsgDidntBlocked(blockingPlayer.getId());
    }


    public void block(GameSession gameSessionContext, Player blockingPlayer, GameSessionCard blockingSessionCard) {
        this.blocking = new PlayerCard(blockingPlayer, blockingSessionCard);
        // TODO: resolve fight and update gamestate (ws)
    }

}
