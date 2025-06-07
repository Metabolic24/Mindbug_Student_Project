package org.metacorp.mindbug.dto.player;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.UUID;

/**
 * DTO for player data
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"uuid", "name"})
public class PlayerLightDTO {
    private UUID uuid;
    private String name;
}





