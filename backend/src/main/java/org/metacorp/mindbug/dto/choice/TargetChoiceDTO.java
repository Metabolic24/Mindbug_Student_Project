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
 * DTO for target choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard", "availableTargets", "targetsCount", "optional"})
public class TargetChoiceDTO extends ChoiceDTO {
    private Set<CardDTO> availableTargets;
    private Integer targetsCount;

    /**
     * Constructor
     *
     * @param playerToChoose   the player to choose
     * @param sourceCard       the card that caused this choice
     * @param availableTargets the set of available targets
     * @param targetsCount     the number of targets to be chosen
     */
    public TargetChoiceDTO(UUID playerToChoose, UUID sourceCard, Set<CardDTO> availableTargets, Integer targetsCount) {
        super(ChoiceType.TARGET, playerToChoose, sourceCard);
        this.availableTargets = availableTargets;
        this.targetsCount = targetsCount;
    }
}
