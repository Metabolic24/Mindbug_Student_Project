package org.metacorp.mindbug.model;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.IChoice;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;

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
    public Game(Player... allPlayers) {
        this.uuid = UUID.randomUUID();
        this.winner = null;
        this.cards = new ArrayList<>();
        this.bannedCards = new ArrayList<>();
        this.evolutionCards = new ArrayList<>();
        this.effectQueue = new EffectQueue();
        
        // Convertit les arguments variables en liste
        this.players = Arrays.asList(allPlayers);
        
        // Si on est 4, on initialise les équipes
        if (allPlayers.length == 4) {
            setupTeams(this.players);
        }
    }

    private void setupTeams(List<Player> players) {
        Team team1 = new Team();
        Team team2 = new Team();
        
        // Joueur 0 et 2 ensemble vs Joueur 1 et 3
        players.get(0).setTeam(team1);
        players.get(2).setTeam(team1);
        
        players.get(1).setTeam(team2);
        players.get(3).setTeam(team2);
    }

    public Player getOpponent() {
        return currentPlayer.getOpponent(players);
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
