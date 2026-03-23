package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MindbugChoice extends AbstractChoice<Boolean> {

    @NonNull
    private CardInstance playedCard;

    @NonNull
    private List<Player> remainingPlayers;

    /**
     * Constructor
     *
     * @param remainingPlayers the list of remaining players still able to choose
     */
    public MindbugChoice(@NonNull CardInstance playedCard, @NonNull List<Player> remainingPlayers) {
        this.playedCard = playedCard;
        this.playerToChoose = remainingPlayers.getFirst();
        this.remainingPlayers = remainingPlayers.subList(1, remainingPlayers.size());
    }

    @Override
    public void resolve(Boolean choice, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveMindbugChoice(choice, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.MINDBUG;
    }
}
