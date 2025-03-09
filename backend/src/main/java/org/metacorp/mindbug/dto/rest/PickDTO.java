package org.metacorp.mindbug.dto.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for card pick request body
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PickDTO extends GameDTO {
    private UUID cardId;
}
