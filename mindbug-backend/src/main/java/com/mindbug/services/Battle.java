package com.mindbug.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mindbug.model.Card;

@Component
@Scope("prototype")
public class Battle {
    private GameSessionCard attackingCard;
    private GameSessionCard blockingCard;

    private BattlePhase battlePhase;

    public Battle() {
        this.battlePhase = BattlePhase.NO_BATTLE;
    }



    public void attack(GameSessionCard attackingCard) {
        this.attacking = attackingCard;
        this.battlePhase = BattlePhase.ATTACKING;
    } 

    public boolean canAttack() {
        if(this.battlePhase == BattlePhase.NO_BATTLE)
    }
}
