package org.metacorp.mindbug.model;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.AbstractChoice;
import org.metacorp.mindbug.model.effect.AfterEffectInterface;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.history.HistoryEntry;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main class that manages game workflow
 */
@Getter
@Setter
public class Game {

    private UUID uuid;
    private Logger logger;

    private List<Player> players;
    private Player currentPlayer;
    private List<Player> winners;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;
    private List<CardInstance> evolutionCards;

    private CardInstance playedCard;
    private CardInstance attackingCard;

    private CardInstance forcedTarget;

    private final EffectQueue effectQueue;
    private AbstractChoice<?> choice;
    private AfterEffectInterface afterEffect;

    private boolean forcedAttack;
    private boolean webSocketUp;
    private final transient int gameMode;

    private List<HistoryEntry> history;

    /**
     * Empty constructor (WARNING: a game is not meant to be reused)
     */
    public Game(List<Player> playersList) {
        uuid = UUID.randomUUID();
        logger = LoggerFactory.getLogger(uuid.toString());

        winners = null;
        cards = new ArrayList<>();
        bannedCards = new ArrayList<>();
        evolutionCards = new ArrayList<>();
        effectQueue = new EffectQueue();
        players = new ArrayList<>(playersList);
        history = new ArrayList<>();

        setupTeams();

        // TODO Remplacer par une enumeration si cette info est vraiment utile
        gameMode = resolveGameMode(players.size());
    }

    private int resolveGameMode(int playersCount) {
        return switch (playersCount) {
            case 2 -> 1;
            case 4 -> 2;
            default -> throw new IllegalStateException("Unsupported number of players: " + playersCount);
        };
    }

    /**
     * Setup players' team
     */
    private void setupTeams() {
        int playersCount = players.size();
        // Check that there is a valid players count
        if (playersCount != 2 && playersCount != 4) {
            throw new IllegalStateException("Unsupported number of players: " + playersCount);
        }

        // Initialize teams
        Team team1 = new Team();
        Team team2 = new Team();

        // Update player team and next player
        for (int i = 0; i < playersCount; i++) {
            Player player = players.get(i);
            player.setTeam(i % 2 == 0 ? team1 : team2);
        }
    }

    /**
     * Returns the game mode of the game
     *  - return 1 : 1v1 game mode
     *  - return 2 : 2v2 game mode
     */
    public int typeGameMode(){
        return gameMode;
    }

    public List<Player> getOpponents() {
        return currentPlayer.getOpponents(players);
    }

    public Player getAlly() {
        return currentPlayer.getAlly(players);
    }

    public boolean isFinished() {
        return winners != null && !winners.isEmpty();
    }

    /**
     * Switches the turn to the next player in circular order.
     * In 2v2 mode, the {@code players} list must be ordered in an alternating pattern
     * (Team A - Player 1, Team B - Player 1, Team A - Player 2, Team B - Player 2)
     * to ensure the turn passes between allies and enemies correctly.
     */
    public void setNextPlayer() {
        int nextPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
        this.setCurrentPlayer(players.get(nextPlayerIndex));
    }

    public void runAfterEffect() throws GameStateException, WebSocketException {
        AfterEffectInterface oldAfterEffect = afterEffect;

        if (afterEffect != null) {
            afterEffect.run();

            if (oldAfterEffect.equals(afterEffect)) {
                afterEffect = null;
            }
        }
    }

    @Override
    public String toString() {
        return "Game{"
                + "currentPlayer=" + currentPlayer.getName()
                + ", finished=" + isFinished()
                + ", bannedCards=" + bannedCards
                + ", playedCard=" + playedCard
                + ", attackingCard=" + attackingCard
                + ", effectQueue=" + effectQueue
                + ", choice=" + choice
                + ", afterEffect=" + afterEffect
                + '}';
    }
}
