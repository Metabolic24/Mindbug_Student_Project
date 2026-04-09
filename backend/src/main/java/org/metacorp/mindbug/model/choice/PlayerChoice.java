package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerChoice extends AbstractChoice<Player> {

    // The card which effect required this choice creation
    @NonNull
    private CardInstance effectSource;

    // The effect that should be resolved after the choice resolution
    @NonNull
    private ResolvableEffect<List<CardInstance>> effect;

    // How many card should the player choose, -1 means he must select all cards
    @NonNull
    private Integer targetsCount;

    

    // we want to target all the cards of a player 
    // so we have a list of list of card, each list of card is the list of cards of a player
    @NonNull
    private List<Player> availableTargets;

    private boolean optional;

    public PlayerChoice(@NonNull Player playerToChoose, @NonNull CardInstance effectSource, @NonNull ResolvableEffect<List<CardInstance>> effect,
                             @NonNull List<Player> availableTargets) {
        this.playerToChoose = playerToChoose;
        this.effectSource = effectSource;
        this.effect = effect;
        this.availableTargets = availableTargets;
        this.targetsCount = 1; // the player must choose only one player, but we keep the targetsCount for the log 
        
    }

    @Override
    public void resolve(Player chosenTargetIds, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolvePlayerChoice(chosenTargetIds, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.PLAYER;
    }
}
