package org.metacorp.mindbug.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.metacorp.mindbug.model.card.CardKeyword;

import java.util.Set;

/**
 * DTO for card data
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonPropertyOrder({"id", "power", "keywords", "evolutionId", "baseCardId", "setName", "unique"})
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class LightCardDTO {
    /**
     * The card ID
     */
    private int id;
    /**
     * The card power
     */
    private int power;
    /**
     * The card keywords
     */
    private Set<CardKeyword> keywords;
    /**
     * The ID of the card that is the evolution of this card
     */
    private Integer evolutionId;
    /**
     * The ID of the corresponding non-evolved card
     */
    private Integer parentId;
    /**
     * The name of the original set that contains this card
     */
    private String setName;
    /**
     * Is there multiple instances of this card in its related cards set
     */
    private boolean unique;
}
