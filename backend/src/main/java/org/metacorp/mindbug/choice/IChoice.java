package org.metacorp.mindbug.choice;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.choice.ChoiceType;

public interface IChoice<T> {

    /**
     * @return the choice type
     */
    ChoiceType getType();

    /**
     * Resolve the current choice
     * @param choiceResolver the data required to resolve the choice
     * @param game the related game
     */
    void resolve(T choiceResolver, Game game) throws GameStateException;
}
