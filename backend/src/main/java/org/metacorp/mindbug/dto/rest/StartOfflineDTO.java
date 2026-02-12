package org.metacorp.mindbug.dto.rest;

import lombok.Data;
import org.metacorp.mindbug.model.CardSetName;

import java.util.UUID;

@Data
public class StartOfflineDTO {
    private UUID playerId;
    private CardSetName cardSetName;
}
