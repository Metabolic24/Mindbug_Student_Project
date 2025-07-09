package org.metacorp.mindbug.dto.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SurrenderDTO extends GameDTO {
    private UUID playerId;
}
