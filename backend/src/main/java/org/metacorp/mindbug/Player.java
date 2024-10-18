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
    private Integer mindBugs;

    private List<EffectTiming> disabledTiming;

    public Player(String name) {
        this.name = name;
        this.team = new Team();

        hand = new ArrayList<>();
        board = new ArrayList<>();
        discardPile = new ArrayList<>();
        drawPile = new ArrayList<>();
        disabledTiming = new ArrayList<>();
        mindBugs = 2;
    }

    public void drawX(int cardsToDraw) {
        while (cardsToDraw != 0 && !drawPile.isEmpty()) {
            hand.add(drawPile.removeFirst());
            cardsToDraw--;
        }
    }

    public void addCardToBoard(CardInstance card, boolean mindBug) {
        if (!mindBug) {
            hand.remove(card);
        }

        board.add(card);
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
