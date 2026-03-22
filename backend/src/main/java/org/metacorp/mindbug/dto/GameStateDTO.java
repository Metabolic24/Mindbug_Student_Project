package org.metacorp.mindbug.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.dto.card.CardDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for game state data
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uuid", "players", "currentPlayerID", "winners", "card", "choice", "forcedAttack"})
public class GameStateDTO {
    @NonNull
    private UUID uuid;
    @NonNull
    private Set<PlayerDTO> players;
    @NonNull
    private UUID currentPlayerID;

    private Set<UUID> winners;

    /**
     * The picked or attacking card if any
     */
    private CardDTO card; // This field may be null

    private AbstractChoiceDTO choice; // This field may be null

    private boolean forcedAttack;

    public PlayerDTO getPlayerById(UUID playerId) {
        return players.stream().filter(playerDTO -> playerDTO.getUuid().equals(playerId)).findFirst().orElse(null);
    }
}
