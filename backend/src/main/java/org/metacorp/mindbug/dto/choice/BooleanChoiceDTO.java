package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.metacorp.mindbug.dto.card.CardDTO;
import org.metacorp.mindbug.model.choice.ChoiceType;

import java.util.UUID;

/**
 * DTO for hunter choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard", "targetCard"})
public class BooleanChoiceDTO extends ChoiceDTO {
    private CardDTO targetCard;

    /**
     * Constructor
     *
     * @param playerToChoose the player to choose
     * @param sourceCard     the card that caused this choice
     * @param targetCard     the card that is concerned by this choice
     */
    public BooleanChoiceDTO(UUID playerToChoose, CardDTO sourceCard, CardDTO targetCard) {
        super(ChoiceType.BOOLEAN, playerToChoose, sourceCard);
        this.targetCard = targetCard;
    }
}
