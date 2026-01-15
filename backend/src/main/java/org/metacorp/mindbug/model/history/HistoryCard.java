package org.metacorp.mindbug.model.history;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

/**
 * Model for a card reference in a history entry
 */
@Data
public class HistoryCard {
    @NonNull
    private UUID uuid;
    @NonNull
    private String name;
    @NonNull
    private UUID owner;
}
