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

    @Override
    public String toString() {
        return "Player (name : " + name + ", team : " + team + ", mindbugs : " + mindBugs + ")";
    }
}
