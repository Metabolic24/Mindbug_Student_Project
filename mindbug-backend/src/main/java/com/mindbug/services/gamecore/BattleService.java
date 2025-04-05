package com.mindbug.services.gamecore;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessagAskBlock;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    Battle battle;

    public void startAttack(GameSession gameSessionContext, Player attackingPlayer, GameSessionCard attackingSessionCard) {
        this.battle.setAttacking(new PlayerCard(attackingPlayer, attackingSessionCard));
        
        gameSessionContext.getGameWsMessageManager().sendMessage(
            new WSMessagAskBlock(
                gameSessionContext.getOpponent().getId(),
                gameSessionContext.getGame()
            )
        );
    }

    public void noBlock(Player blockingPlayer) {
        this.battle.setBlocking(new PlayerCard(blockingPlayer));
    }

    public void block(Player blockingPlayer, GameSessionCard blockingSessionCard) {
        this.battle.setBlocking(new PlayerCard(blockingPlayer, blockingSessionCard));
    }
}
