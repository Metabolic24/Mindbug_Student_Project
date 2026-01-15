package org.metacorp.mindbug.model.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Model for history entry
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class HistoryEntry {
    @NonNull
    private HistoryKey key;
    private HistoryCard source;
    private List<HistoryCard> targets;
    private Map<String, Object> data;

    public HistoryEntry(@NonNull HistoryKey key, HistoryCard source,  List<HistoryCard> targets, Map<String, Object> data) {
        this.key = key;
        this.source = source;
        this.targets = targets;
        this.data = data;
    }
}
