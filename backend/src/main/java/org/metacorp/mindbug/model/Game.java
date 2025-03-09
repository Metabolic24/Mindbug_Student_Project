package org.metacorp.mindbug.model;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.player.Player;

import java.util.*;

/**
 * Main class that manages game workflow
 */
@Getter
@Setter
public class Game {

    private UUID uuid;

    private List<Player> players;
    private Player currentPlayer;
    private boolean finished;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;

    private CardInstance playedCard;
    private CardInstance attackingCard;

    private final EffectQueue effectQueue;
    private IChoice<?> choice;
    private Runnable afterEffect;

    /**
     * Empty constructor (WARNING : a game is not meant to be reused)
     */
    public Game(String player1, String player2) {
        uuid = UUID.randomUUID();
        finished = false;
        bannedCards = new ArrayList<>();
        effectQueue = new EffectQueue();
        players = Arrays.asList(new Player(player1), new Player(player2));
    }

    public Player getOpponent() {
        return currentPlayer.getOpponent(players);
    }

    public void runAfterEffect() {
        if (afterEffect != null) {
            afterEffect.run();
            afterEffect = null;
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "currentPlayer=" + currentPlayer.getName() +
                ", finished=" + finished +
                ", bannedCards=" + bannedCards +
                ", playedCard=" + playedCard +
                ", attackingCard=" + attackingCard +
                ", effectQueue=" + effectQueue +
                ", choice=" + choice +
                ", afterEffect=" + afterEffect +
                '}';
    }
}
