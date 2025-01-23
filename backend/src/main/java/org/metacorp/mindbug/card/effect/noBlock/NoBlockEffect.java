package org.metacorp.mindbug.card.effect.noBlock;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.choice.Choice;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;

/** Effect that forbids one or more cards to block */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoBlockEffect extends AbstractEffect implements ResolvableEffect {
    public final static String TYPE = "NO_BLOCK";

    /**
     * The number of cards that will be unable to block, -1 for all
     */
    private int value;
    /**
     * The maximum power for cards that will be unable to block
     */
    private Integer max;
    /**
     * Should the highest creatures be unable to block
     */
    private boolean highest;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (highest) {
            for (CardInstance highestCard : opponent.getHighestCards()) {
                highestCard.setCanBlock(false);
            }
        } else if (max != null) {
            for (CardInstance opponentCard : opponent.getBoard()) {
                if (opponentCard.getPower() <= max) {
                    opponentCard.setCanBlock(false);
                }
            }
        } else if (opponent.getBoard().size() <= value){
            for (CardInstance opponentCard : opponent.getBoard()) {
                opponentCard.setCanBlock(false);
            }
        } else {
            game.setChoiceList(new ChoiceList(card.getOwner(), value, Choice.getChoicesFromCards(opponent.getBoard(), ChoiceLocation.BOARD), this, card));
        }
    }

    @Override
    public void resolve(ChoiceList choices) {
        //TODO To be implemented
    }
}
