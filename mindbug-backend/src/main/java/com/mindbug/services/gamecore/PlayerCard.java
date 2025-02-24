package com.mindbug.services.gamecore;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;

public class PlayerCard {
    private Player player;
    private GameSessionCard card;


    public PlayerCard(Player player, GameSessionCard card) {
        this.player = player;
        this.card = card;
    }


    public Player getPlayer() {
        return player;
    }


    public void setPlayer(Player player) {
        this.player = player;
    }


    public GameSessionCard getCard() {
        return card;
    }


    public void setCard(GameSessionCard card) {
        this.card = card;
    }

    

    
}
