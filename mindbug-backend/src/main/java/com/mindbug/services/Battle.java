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

    public void attack(GameSessionCard attackingCard) {
        this.attacking = attackingCard;
    }
}
