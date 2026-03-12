package org.metacorp.mindbug.dto.ws;

import lombok.Data;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for game state data related to a specific player
 */
@Data
public class WsPlayerGameState {

    private UUID uuid;
    private PlayerDTO player;
    private PlayerDTO ally;
    private PlayerDTO opponent;
    private List<PlayerDTO> opponents;
    private Boolean playerTurn;
    private List<UUID> winners;

    private CardDTO card;
    private AbstractChoiceDTO choice;

    /**
     * Constructor
     *
     * @param gameState the GameStateDTO to transform
     * @param playerId  the player receiving this state
     */
    public WsPlayerGameState(GameStateDTO gameState, UUID playerId) {
        this.uuid = gameState.getUuid();
        this.playerTurn = playerId.equals(gameState.getPlayer().getUuid());
        this.winners = gameState.getWinners();
        this.card = gameState.getCard();
        this.choice = gameState.getChoice();

        Map<UUID, PlayerDTO> playerById = new LinkedHashMap<>();
        addIfNotNull(playerById, gameState.getPlayer());
        addIfNotNull(playerById, gameState.getAlly());
        for (PlayerDTO stateOpponent : gameState.getOpponents()) {
            addIfNotNull(playerById, stateOpponent);
        }

        PlayerDTO current = playerById.get(playerId);
        if (current == null) {
            throw new IllegalArgumentException("Unknown player in game state: " + playerId);
        }
        this.player = new PlayerDTO(current);

        this.ally = null;
        this.opponents = new ArrayList<>();
        for (PlayerDTO candidate : playerById.values()) {
            if (candidate.getUuid().equals(this.player.getUuid())) {
                continue;
            }

            if (candidate.getTeamId().equals(this.player.getTeamId())) {
                this.ally = new PlayerDTO(candidate);
            } else {
                this.opponents.add(withHiddenHand(candidate));
            }
        }

        this.opponent = opponents.isEmpty() ? null : opponents.getFirst();
    }

    private static void addIfNotNull(Map<UUID, PlayerDTO> players, PlayerDTO player) {
        if (player != null) {
            players.put(player.getUuid(), player);
        }
    }

    private static PlayerDTO withHiddenHand(PlayerDTO player) {
        PlayerDTO hiddenPlayer = new PlayerDTO(player);
        List<CardDTO> hiddenHand = new ArrayList<>(player.getHand().size());
        for (CardDTO cardDTO : player.getHand()) {
            CardDTO hiddenCard = new CardDTO();
            hiddenCard.setUuid(cardDTO.getUuid());
            hiddenHand.add(hiddenCard);
        }
        hiddenPlayer.setHand(hiddenHand);
        return hiddenPlayer;
    }
}
