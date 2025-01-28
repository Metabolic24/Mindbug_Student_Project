package org.metacorp.mindbug.card.effect.steal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.choice.bool.BooleanChoice;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Effect that steals card from the opponent hand or board depending on several conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StealEffect extends AbstractEffect {
    public final static String TYPE = "STEAL";

    private int value;              // The number of cards to steal
    private Integer min;            // The minimum power for card(s) to be stolen
    private Integer max;            // The maximum power for card(s) to be stolen
    private boolean random;         // Should stolen card(s) be chosen randomly
    private StealSource source;     // From where should card(s) be stolen

    private boolean mustPlay;       // Should stolen card(s) be played in this effect resolution
    private boolean mayPlay;        // May the stolen card(s) be played in this effect resolution

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player currentPlayer = card.getOwner();
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        List<CardInstance> availableCards = switch (source) {
            case DISCARD -> opponent.getDiscardPile();
            case HAND -> opponent.getHand();
            case SELF_DISCARD -> currentPlayer.getDiscardPile();
            case BOARD -> opponent.getBoard();
        };

        if (min != null) {
            availableCards = availableCards.stream().filter(currentCard -> currentCard.getPower() >= min).collect(Collectors.toList());
        }

        if (max != null) {
            availableCards = availableCards.stream().filter(currentCard -> currentCard.getPower() <= max).collect(Collectors.toList());
        }

        int cardsCount = availableCards.size();
        if (cardsCount > 0) {
            if (cardsCount <= value) {
                stealCards(new ArrayList<>(availableCards), game);
            } else if (random) {
                List<CardInstance> stolenCards = new ArrayList<>();
                Random random = new Random();
                for (int i = 1; i <= value; i++) {
                    int index = random.nextInt(cardsCount - i);
                    stolenCards.add(availableCards.remove(index));

                }
                stealCards(stolenCards, game);
            } else {
                game.setCurrentChoice(new TargetChoice(opponent, card, new TargetStealEffect(this, game), value, new HashSet<>(availableCards)));
            }
        }
    }

    protected void stealCards(List<CardInstance> stolenCards, Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        for (CardInstance stolenCard : stolenCards) {
            Player cardOwner = stolenCard.getOwner();
            switch (source) {
                case DISCARD, SELF_DISCARD -> cardOwner.getDiscardPile().remove(stolenCard);
                case HAND -> cardOwner.getHand().remove(stolenCard);
                case BOARD -> cardOwner.getBoard().remove(stolenCard);
            }

            stolenCard.setOwner(currentPlayer);

            if (mustPlay) {
                currentPlayer.getBoard().add(stolenCard);
            } else if (mayPlay) {
                game.setCurrentChoice(new BooleanChoice(currentPlayer, stolenCard, new BooleanStealEffect(this)));
            } else {
                currentPlayer.getHand().add(stolenCard);
            }
        }
    }
}
