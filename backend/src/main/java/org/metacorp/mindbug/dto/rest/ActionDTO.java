package org.metacorp.mindbug.dto.rest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for action request body
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({"gameId", "actionCardId"})
public class ActionDTO extends GameDTO {
    private UUID actionCardId;
}
