package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.model.choice.ChoiceType;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for hunter choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard", "availableTargets"})
public class HunterChoiceDTO extends ChoiceDTO {
    private Set<CardDTO> availableTargets;

    /**
     * Constructor
     *
     * @param playerToChoose   the player to choose
     * @param sourceCard       the card that caused this choice
     * @param availableTargets the set of available targets
     */
    public HunterChoiceDTO(UUID playerToChoose, CardDTO sourceCard, Set<CardDTO> availableTargets) {
        super(ChoiceType.HUNTER, playerToChoose, sourceCard);
        this.availableTargets = availableTargets;
    }
}
