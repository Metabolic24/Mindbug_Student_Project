package org.metacorp.mindbug;

import lombok.Data;

import java.util.*;

/** Class that describes a player data */
@Data
public class Player {
    private static int PLAYER_COUNT = 0;

    private String name;
    private Team team;

    private List<CardInstance> hand;
    private List<CardInstance> board;
    private List<CardInstance> drawPile;
    private List<CardInstance> discardPile;
    private Integer mindBugs;

    private List<EffectTiming> disabledTiming;

    public Player(Team team) {
        PLAYER_COUNT++;
        this.name = "Player" + PLAYER_COUNT;
        this.team = team;

        hand = new ArrayList<>();
        board = new ArrayList<>();
        discardPile = new ArrayList<>();
        drawPile = new ArrayList<>();
        disabledTiming = new ArrayList<>();
        mindBugs = 2;
    }

    @Override
    public String toString() {
        return "Player (name : " + name + ", team : " + team + ", mindbugs : " + mindBugs + ")";
    }
}
