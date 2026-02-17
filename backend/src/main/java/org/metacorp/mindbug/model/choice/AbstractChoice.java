package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;

@Data
@NoArgsConstructor
public abstract class AbstractChoice<T> {

    @NonNull
    protected Player playerToChoose;

    /**
     * @return the choice type
     */
     public abstract ChoiceType getType();

    /**
     * Resolve the current choice
     *
     * @param choiceData the data required to resolve the choice
     * @param game       the related game
     */
    public abstract void resolve(T choiceData, Game game) throws GameStateException, WebSocketException;
}
