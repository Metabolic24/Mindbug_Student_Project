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
        

        gameSessionContext.sendWSMsgBlocked(blockingPlayer.getId(), blockingSessionCard.getId());
    }

    public void resoleBattle(GameSession gameSessionContext) {
        Player opponent = this.blocking.getPlayer();
        Player attacker = this.attacking.getPlayer();

        if(this.blocking.getCard() == null) {
            // Opponent didnt blocked. Remove one life point
            opponent.setLifepoints(opponent.getLifepoints() - 1);
            
        } else {
            // Opponent blocked. Compare monster power
            GameSessionCard oppoenentMonster = this.blocking.getCard();
            GameSessionCard attackerMonster = this.attacking.getCard();

            if (oppoenentMonster.getCard().getPower() < attackerMonster.getCard().getPower()) {
                // opponent monster is weaker. destroy it.
                gameSessionContext.destroyCard(oppoenentMonster.getId(), opponent.getId());

            } else if (oppoenentMonster.getCard().getPower() > attackerMonster.getCard().getPower()) {
                // attacker monster is weaker. destroy it.
                gameSessionContext.destroyCard(attackerMonster.getId(), attacker.getId());
            } else {
                // Equals power. Destroy both of them
                gameSessionContext.destroyCard(oppoenentMonster.getId(), opponent.getId());
                gameSessionContext.destroyCard(attackerMonster.getId(), attacker.getId());
            }
            
        }
    }

}
