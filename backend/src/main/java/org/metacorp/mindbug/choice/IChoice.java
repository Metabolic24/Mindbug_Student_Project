package org.metacorp.mindbug.choice;

import org.metacorp.mindbug.game.Game;

public interface IChoice<T> {

    /**
     * @return the choice type
     */
    ChoiceType getType();

    /**
     * Resolve the current choice
     * @param game the related game
     * @param choiceResolver the data required to resolve the choice
     */
    void resolve(Game game, T choiceResolver);
}
