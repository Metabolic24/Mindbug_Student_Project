package org.metacorp.mindbug;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Utility class
public final class Utils {

    private Utils() {
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
}
