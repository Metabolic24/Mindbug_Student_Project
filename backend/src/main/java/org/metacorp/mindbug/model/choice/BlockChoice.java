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
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class BlockChoice extends AbstractChoice<UUID> {

    @NonNull
    private CardInstance attackingCard;

    @NonNull
    private List<Player> remainingPlayers;

    @NonNull
    private Map<Player, List<CardInstance>> blockersMap;

    /**
     * Constructor
     *
     * @param remainingPlayers the list of remaining players still able to choose
     */
    public BlockChoice(@NonNull CardInstance attackingCard, @NonNull List<Player> remainingPlayers, @NonNull Map<Player, List<CardInstance>> blockersMap) {
        System.out.println("Creating block choice with attacking card " + attackingCard.getCard().getName() + " and remaining players " + remainingPlayers.stream().map(Player::getName).toList());//TODO Remove this debug when block choice will be fully tested
        this.attackingCard = attackingCard;
        this.playerToChoose = remainingPlayers.getFirst();
        this.remainingPlayers = remainingPlayers.subList(1, remainingPlayers.size());
        this.blockersMap = blockersMap;
    }

    @Override
    public void resolve(UUID choice, Game game) throws GameStateException, WebSocketException {
        System.out.println("Choice Resolving block choice...");//TODO Remove this debug when block choice will be fully tested
        ChoiceUtils.resolveBlockChoice(choice, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.BLOCK;
    }
}
