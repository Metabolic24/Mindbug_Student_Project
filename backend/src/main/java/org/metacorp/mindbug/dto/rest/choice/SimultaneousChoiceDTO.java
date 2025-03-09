package org.metacorp.mindbug.dto.rest.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.dto.GameDTO;

import java.util.UUID;

/**
 * DTO for the body of the simultaneous choice resolution request
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SimultaneousChoiceDTO extends GameDTO {
    private UUID firstCardId;
}
