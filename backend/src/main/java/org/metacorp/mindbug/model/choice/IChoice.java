package org.metacorp.mindbug.model.choice;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;

public interface IChoice<T> {

    /**
     * @return the choice type
     */
    ChoiceType getType();

    /**
     * Resolve the current choice
     * @param choiceData the data required to resolve the choice
     * @param game the related game
     */
    void resolve(T choiceData, Game game) throws GameStateException;
}
