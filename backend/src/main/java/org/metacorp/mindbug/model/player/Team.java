package org.metacorp.mindbug.model.player;

import lombok.Getter;
import lombok.Setter;

/** Class that describes a team of players */
@Getter
@Setter
public class Team {
    private int lifePoints;

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
