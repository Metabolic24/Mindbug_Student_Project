package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.model.choice.ChoiceType;

import java.util.Set;

/**
 * DTO for simultaneous choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard", "availableEffects"})
public class SimultaneousChoiceDTO extends AbstractChoiceDTO {
    private Set<CardDTO> availableEffects;

    /**
     * Constructor
     *
     * @param availableEffects the set of all simultaneous effects
     */
    public SimultaneousChoiceDTO(Set<CardDTO> availableEffects) {
        super(ChoiceType.SIMULTANEOUS);
        this.availableEffects = availableEffects;
    }
}
