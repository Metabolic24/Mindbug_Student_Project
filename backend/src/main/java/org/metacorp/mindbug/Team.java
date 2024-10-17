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
}
