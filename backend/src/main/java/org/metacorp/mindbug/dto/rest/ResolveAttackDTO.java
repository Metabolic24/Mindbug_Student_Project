package org.metacorp.mindbug.dto.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for attack resolution request body
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResolveAttackDTO extends GameDTO {
    private UUID defendingPlayerId;
    private UUID defenseCardId;
}
