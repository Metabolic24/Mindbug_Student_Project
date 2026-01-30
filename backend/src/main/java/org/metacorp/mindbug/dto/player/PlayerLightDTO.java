package org.metacorp.mindbug.dto.player;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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





