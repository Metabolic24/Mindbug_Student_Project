package org.metacorp.mindbug.dto.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for card played request body
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayDTO extends GameDTO {
    private UUID mindbuggerId;
}
