package org.metacorp.mindbug.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Abstract DTO to be used when a game ID is needed
 */
@Getter
@Setter
public class GameDTO {
    private UUID gameId;

    /**
     * Constructor
     */
    protected GameDTO() {
        // To be used only by child classes
    }
}
