package org.metacorp.mindbug.dto.rest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for attack declaration request body
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({"gameId", "attackingCardId"})
public class DeclareAttackDTO extends GameDTO {
    private UUID attackingCardId;
}
