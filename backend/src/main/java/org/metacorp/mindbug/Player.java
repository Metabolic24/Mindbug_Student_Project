package org.metacorp.mindbug;

import lombok.Data;

import java.util.*;

/** Class that describes a player data */
@Data
public class Player {

    private String name;
    private Team team;

    private List<CardInstance> hand;
    private List<CardInstance> board;
    private List<CardInstance> drawPile;
    private List<CardInstance> discardPile;
    private int mindBugs;

    private Set<EffectTiming> disabledTiming;

    public Player(String name) {
        this.name = name;
        this.team = new Team();

        hand = new ArrayList<>();
        board = new ArrayList<>();
        discardPile = new ArrayList<>();
        drawPile = new ArrayList<>();
        disabledTiming = new HashSet<>();
        mindBugs = 2;
    }

    public boolean hasMindbug() {
        return mindBugs > 0;
    }

    public boolean canTrigger(EffectTiming timing) {
        return !this.disabledTiming.contains(timing);
    }

    public void disableTiming(EffectTiming timing) {
        this.disabledTiming.add(timing);
    }

    public void drawX(int cardsToDraw) {
        while (cardsToDraw != 0 && !drawPile.isEmpty()) {
            hand.add(drawPile.removeFirst());
            cardsToDraw--;
        }
    }

    public void addCardToBoard(CardInstance card, boolean mindBug) {
        if (mindBug) {
            mindBugs--;
        } else {
            hand.remove(card);
        }

        board.add(card);
    }

    public void addCardToDiscardPile(CardInstance card) {
        board.remove(card);
        discardPile.add(card);
    }

    public Player getOpponent(List<Player> players) {
        Player opponent = null;
        for (Player player : players) {
            if (player.getTeam() != team) {
                opponent = player;
            }
        }

        return opponent;
    }

    public List<CardInstance> getHighestCards() {
        List<CardInstance> highestCards = new ArrayList<>();
        int highestPower = 1;

        for (CardInstance card : board) {
            if (card.getPower() > highestPower) {
                highestPower = card.getPower();
                highestCards.clear();
                highestCards.add(card);
            } else if (card.getPower() == highestPower) {
                highestCards.add(card);
            }
        }

        return highestCards;
    }

    public List<CardInstance> getLowestCards() {
        return getLowestCards(10);
    }

    public List<CardInstance> getLowestCards(int lowestPower) {
        List<CardInstance> lowestCards = new ArrayList<>();

        for (CardInstance card : board) {
            if (card.getPower() < lowestPower) {
                lowestPower = card.getPower();
                lowestCards.clear();
                lowestCards.add(card);
            } else if (card.getPower() == lowestPower) {
                lowestCards.add(card);
            }
        }

        return lowestCards;
    }

    @Override
    public String toString() {
        return "Player (name : " + name + ", team : " + team + ", mindbugs : " + mindBugs + ")";
    }
}
