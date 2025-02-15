package org.metacorp.mindbug.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Utility class
public final class CardUtils {

    public static final String FIRST_CONTACT = "FIRST_CONTACT";


    private CardUtils() {
        // Not to be used
    }

    public static List<CardInstance> getCardsFromConfig(String filename) {
        // Open an input stream for the given configuration file
        try (InputStream is = Game.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found");
            }

            // Unmarshal file content into a list of Card
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Card> fileCards = objectMapper.readValue(is, typeFactory.constructCollectionType(List.class, Card.class));

            // Transform the List of Card into a List of CardInstance
            List<CardInstance> cards = new ArrayList<>();
            for (Card fileCard : fileCards) {
                cards.add(new CardInstance(fileCard));

                if (!fileCard.isUnique()) {
                    cards.add(new CardInstance(fileCard));
                }
            }

            return cards;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Get the cards with the lowest power on boards
    public static List<CardInstance> getLowestCards(List<Player> players) {
        List<CardInstance> lowestCards = new ArrayList<>();
        int lowestPower = 10;

        for (Player player : players) {
            List<CardInstance> currentCards = player.getLowestCards(lowestPower);

            if (!currentCards.isEmpty()) {
                int currentPower = currentCards.getFirst().getPower();

                if (currentPower < lowestPower) {
                    lowestPower = currentPower;
                    lowestCards = new ArrayList<>(currentCards);
                } else if (currentPower == lowestPower) {
                    lowestCards.addAll(currentCards);
                }
            }
        }

        return lowestCards;
    }
}
