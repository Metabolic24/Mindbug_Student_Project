package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.choice.ChoiceType;

/**
 * DTO for abstract choice data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using=ChoiceDTODeserializer.class)
public class AbstractChoiceDTO {
    private ChoiceType type;
}
