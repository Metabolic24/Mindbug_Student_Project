package org.metacorp.mindbug.choice;

import lombok.*;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.card.effect.ResolvableEffect;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChoiceList {
    @NonNull
    private Player playerToChoose;      // The player that must make the choice
    @NonNull
    private Integer choicesCount;           // How many card should the player choose, -1 means he must select all cards
    @NonNull
    private List<Choice> choices;       // The list of available choices

    private ResolvableEffect sourceEffect;        // The effect that triggered the choice if any
    private CardInstance sourceCard;    // The card that triggered the choice if any
}
