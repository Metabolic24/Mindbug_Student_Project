package org.metacorp.mindbug.model;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Main class that manages game workflow
 */
@Getter
@Setter
public class Game {

    private UUID uuid;

    private List<Player> players;
    private Player currentPlayer;
    private Player winner;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;
    private List<CardInstance> evolutionCards;

    private CardInstance playedCard;
    private CardInstance attackingCard;
    private CardInstance forcedTarget;

    private final EffectQueue effectQueue;
    private IChoice<?> choice;
    private Runnable afterEffect;

    private boolean forcedAttack;
    private boolean webSocketUp;

    /**
     * Empty constructor (WARNING: a game is not meant to be reused)
     */
    public Game(Player player1, Player player2) {
        uuid = UUID.randomUUID();
        winner = null;
        cards = new ArrayList<>();
        bannedCards = new ArrayList<>();
        evolutionCards = new ArrayList<>();
        effectQueue = new EffectQueue();
        players = Arrays.asList(player1, player2);
    }

    public List<Player> getOpponent() {
        return currentPlayer.getOpponent(players);
    }

    public Player getAllie(){
        return currentPlayer.getAllie(players);
    }

    /**
     * Returns the game mode of the game
     *  - return 1 : 1v1 game mode
     *  - return 2 : 2v2 game mode
     *  - return 0 : Error, issue the game
     */
    public int typeGameMode(){
        if (players.size() == 2){
            return 1;
        }
        if (players.size() == 4){
            return 2;
        }
        return 0;
    }

    public boolean isFinished() {
        return winner != null;
    }

    public void runAfterEffect() {
        Runnable oldAfterEffect = afterEffect;

        if (afterEffect != null) {
            afterEffect.run();

            if (oldAfterEffect.equals(afterEffect)) {
                afterEffect = null;
            }
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "currentPlayer=" + currentPlayer.getName() +
                ", finished=" + isFinished() +
                ", bannedCards=" + bannedCards +
                ", playedCard=" + playedCard +
                ", attackingCard=" + attackingCard +
                ", effectQueue=" + effectQueue +
                ", choice=" + choice +
                ", afterEffect=" + afterEffect +
                '}';
    }
}
