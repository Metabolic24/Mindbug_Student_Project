package org.metacorp.mindbug.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;

import java.util.UUID;

/**
 * DTO for game state data
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uuid", "winner", "player", "opponent", "card", "choice"})
public class GameStateDTO {
    @NonNull
    private UUID uuid;
    @NonNull
    private PlayerDTO player;
    @NonNull
    private PlayerDTO opponent;

    private UUID winner;

    /**
     * The picked or attacking card if any
     */
    private CardDTO card; // This field may be null

    private AbstractChoiceDTO choice; // This field may be null
}

