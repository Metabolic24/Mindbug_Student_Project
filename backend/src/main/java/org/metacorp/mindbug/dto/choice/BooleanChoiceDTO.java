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
 * DTO for hunter choice data
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"type", "playerToChoose", "sourceCard", "message"})
public class BooleanChoiceDTO extends ChoiceDTO {
    private String message;

    /**
     * Constructor
     *
     * @param playerToChoose the player to choose
     * @param sourceCard     the card that caused this choice
     * @param targetCard     the card that is concerned by this choice
     */
    public BooleanChoiceDTO(UUID playerToChoose, CardDTO sourceCard, CardDTO targetCard) {
        super(ChoiceType.BOOLEAN, playerToChoose, targetCard);
        switch (sourceCard.getId()) {// TODO C'est pas propre
            case 40:
                message = "Do you want to play the stolen card?";
                break;
            case 41:
                message = "Do you want to revive this card?";
                break;
        }
    }

    /**
     * Constructor
     *
     * @param playerToChoose the player to choose
     * @param targetCard     the card that is concerned by this choice
     * @param message        the message to display
     */
    public BooleanChoiceDTO(UUID playerToChoose, CardDTO targetCard, String message) {
        super(ChoiceType.BOOLEAN, playerToChoose, targetCard);
        this.message = message;
    }
}
