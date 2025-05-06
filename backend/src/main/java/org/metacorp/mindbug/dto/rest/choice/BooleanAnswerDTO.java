package org.metacorp.mindbug.dto.rest.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.dto.GameDTO;

/**
 * DTO for the body of the boolean choice resolution request
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BooleanAnswerDTO extends GameDTO {
    private Boolean ok;
}
