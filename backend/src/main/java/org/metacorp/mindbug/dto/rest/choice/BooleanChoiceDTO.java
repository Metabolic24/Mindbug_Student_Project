package org.metacorp.mindbug.dto.rest.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.dto.GameDTO;

@EqualsAndHashCode(callSuper = true)
@Data
public class BooleanChoiceDTO extends GameDTO {
    private Boolean ok;
}
