package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;

@Data
public class BooleanChoiceResolver {
    @NonNull
    private Player playerToChoose;

    @NonNull
    private CardInstance card;

    @NonNull
    private Boolean choice;
}
