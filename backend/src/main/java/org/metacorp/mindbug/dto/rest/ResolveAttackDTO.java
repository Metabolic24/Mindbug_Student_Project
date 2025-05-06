package org.metacorp.mindbug.dto.rest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for attack resolution request body
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({"gameId", "defendingPlayerId", "defenseCardId"})
public class ResolveAttackDTO extends GameDTO {
    private UUID defendingPlayerId;
    private UUID defenseCardId;
}
