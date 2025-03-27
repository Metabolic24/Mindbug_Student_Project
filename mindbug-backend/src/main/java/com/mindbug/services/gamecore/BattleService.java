package com.mindbug.services.gamecore;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    private final Battle battle = new Battle();

    public void attack(GameSession gameSessionContext, Player attackingPlayer, GameSessionCard attackingSessionCard) {
        battle.setAttacking(new PlayerCard(attackingPlayer, attackingSessionCard));

        gameSessionContext.sendWSMsgAttacked(attackingPlayer.getId(), attackingSessionCard.getId());

        if (gameSessionContext.getOpponent().getBattlefield().isEmpty()) {
            directAttack(gameSessionContext, gameSessionContext.getOpponent());
        } else {
            gameSessionContext.sendWSMsgAskBlock(gameSessionContext.getOpponent().getId());
        }
    }

    public void dontBlock(GameSession gameSessionContext, Player blockingPlayer) {
        battle.setBlocking(new PlayerCard(blockingPlayer));

        gameSessionContext.sendWSMsgDidntBlocked(blockingPlayer.getId());

        resolveBattle(gameSessionContext);
    }

    public void block(GameSession gameSessionContext, Player blockingPlayer, GameSessionCard blockingSessionCard) {
        battle.setBlocking(new PlayerCard(blockingPlayer, blockingSessionCard));

        gameSessionContext.sendWSMsgBlocked(blockingPlayer.getId(), blockingSessionCard.getId());

        resolveBattle(gameSessionContext);
    }

    public void directAttack(GameSession gameSessionContext, Player opponent) {
        opponent.setLifepoints(opponent.getLifepoints() - 1);
        gameSessionContext.sendWSMsgPlayerLifeUpdated(opponent.getId());
    }

    public void resolveBattle(GameSession gameSessionContext) {
        Player opponent = battle.getBlocking().getPlayer();
        Player attacker = battle.getAttacking().getPlayer();

        GameSessionCard attackerCard = battle.getAttacking().getCard();
        GameSessionCard blockingCard = battle.getBlocking().getCard();

        if (blockingCard == null) {
            opponent.setLifepoints(opponent.getLifepoints() - 1);
            gameSessionContext.sendWSMsgPlayerLifeUpdated(opponent.getId());
        } else {
            int attackerPower = attackerCard.getCard().getPower();
            int blockerPower = blockingCard.getCard().getPower();

            if (blockerPower < attackerPower) {
                gameSessionContext.destroyCard(blockingCard, opponent);
            } else if (blockerPower > attackerPower) {
                gameSessionContext.destroyCard(attackerCard, attacker);
            } else {
                gameSessionContext.destroyCard(blockingCard, opponent);
                gameSessionContext.destroyCard(attackerCard, attacker);
            }
        }
    }
}
