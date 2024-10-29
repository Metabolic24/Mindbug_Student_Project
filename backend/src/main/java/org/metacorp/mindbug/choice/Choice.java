package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.CardInstance;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Choice {
    @NonNull
    private CardInstance card;
    @NonNull
    private ChoiceLocation location;

    public static List<Choice> getChoicesFromCards(List<CardInstance> cards, ChoiceLocation location) {
        return cards.stream()
                .map(opponentCard -> new Choice(opponentCard, location))
                .collect(Collectors.toList());
    }
}
