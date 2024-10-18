package org.metacorp.mindbug;

import lombok.Data;

/** Class that describes a team of players */
@Data
public class Team {
    private int lifePoints;

    /**
     * Default constructor (life points are initialized to 3)
     */
    public Team() {
        lifePoints = 3;
    }

    public void gainLifePoints(int amount) {
        lifePoints += amount;
    }

    public void loseLifePoints(int amount) {
        lifePoints -= amount;
        if (lifePoints < 0) {
            lifePoints = 0;
        }
    }
}
