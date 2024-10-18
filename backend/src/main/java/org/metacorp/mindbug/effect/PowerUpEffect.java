package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

/**
 * Effect that increase the power of one or more cards depending on several conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PowerUpEffect extends Effect {
    public final static String TYPE = "POWER_UP";

    /**
     * The power that should be gained by the card(s)
     */
    private int value;
    /**
     * Should the current card be affected or not (default: true)
     */
    private boolean self = true;
    /**
     * Should allies be affected or not
     */
    private boolean allies;
    /**
     * The required amount of life points so the power gain is applied
     */
    private Integer lifePoints;
    /**
     * Should the power gain be applied only if card is alone
     */
    private boolean alone;
    /**
     * Should power be gained for each opponent card
     */
    private boolean byEnemy;
    /**
     * Should the power gain be only available on the owner turn
     */
    private boolean selfTurn;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player currentPlayer = card.getOwner();
        int powerToAdd = value;

        if ((lifePoints != null && currentPlayer.getTeam().getLifePoints() > lifePoints) ||
                (alone && currentPlayer.getBoard().size() != 1) ||
                (selfTurn && !currentPlayer.equals(game.getCurrentPlayer()))) {
            return;
        }

        if (byEnemy) {
            int opponentBoardSize = currentPlayer.getOpponent(game.getPlayers()).getBoard().size();
            powerToAdd *= opponentBoardSize;
        }

        if (self) {
            card.changePower(powerToAdd);
        }

        if (allies) {
            for (CardInstance currentCard : currentPlayer.getBoard()) {
                if (!(currentCard.equals(card))) {
                    currentCard.changePower(powerToAdd);
                }
            }
        }
    }
}
