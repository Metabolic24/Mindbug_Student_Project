package org.metacorp.mindbug.choice.bool;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;

@Data
public class BooleanChoiceResolver {
    @NonNull
    private Player playerToChoose;

    @NonNull
    private CardInstance card;

    @NonNull
    private Boolean choice;
}
