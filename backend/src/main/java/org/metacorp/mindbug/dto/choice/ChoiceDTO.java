package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.model.choice.ChoiceType;

import java.util.UUID;

/**
 * DTO for generic choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard"})
public class ChoiceDTO extends AbstractChoiceDTO {
    private CardDTO sourceCard;

    /**
     * Constructor
     *
     * @param type           the choice type
     * @param playerToChoose the player to choose
     * @param sourceCard     the card that caused this choice
     */
    public ChoiceDTO(ChoiceType type, UUID playerToChoose, CardDTO sourceCard) {
        super(type, playerToChoose);
        this.sourceCard = sourceCard;
    }
}
