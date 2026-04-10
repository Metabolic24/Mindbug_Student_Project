package org.metacorp.mindbug.dto.ws;

import lombok.Data;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.card.CardDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for game state data related to a specific player
 */
@Data
public class WsPlayerGameState {

    private UUID uuid;
    private PlayerDTO player;
    private PlayerDTO ally;
    private Set<PlayerDTO> opponents;

    private UUID currentPlayerID;
    private Set<UUID> winners;

    private AbstractChoiceDTO choice;
    private boolean forcedAttack;

    /**
     * Constructor
     *
     * @param gameState the GameStateDTO to transform
     * @param playerId  the player receiving this state
     */
    public WsPlayerGameState(GameStateDTO gameState, UUID playerId) {
        this.uuid = gameState.getUuid();
        this.player = gameState.getPlayerById(playerId);

        this.currentPlayerID = gameState.getCurrentPlayerID();
        this.winners = gameState.getWinners();

        this.choice = gameState.getChoice();
        this.forcedAttack = gameState.isForcedAttack();

        this.opponents = new HashSet<>();
        for (PlayerDTO candidate : gameState.getPlayers()) {
            if (candidate.getTeamId().equals(this.player.getTeamId()) && (!candidate.getUuid().equals(this.player.getUuid()))) {
                this.ally = withHiddenHand(candidate);
            } else if(!candidate.getTeamId().equals(this.player.getTeamId())) {
                this.opponents.add(withHiddenHand(candidate));
            }
        }
    }

    private static PlayerDTO withHiddenHand(PlayerDTO player) {
        PlayerDTO hiddenPlayer = new PlayerDTO(player);
        hiddenPlayer.setHand(player.getHand().stream()
                .map(cardDTO -> {
                    CardDTO hiddenCard = new CardDTO();
                    hiddenCard.setUuid(cardDTO.getUuid());
                    return hiddenCard;
                }).toList());

        return hiddenPlayer;
    }
}
