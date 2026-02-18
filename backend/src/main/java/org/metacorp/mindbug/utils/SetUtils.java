package org.metacorp.mindbug.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for card sets
 */
public final class SetUtils {

    private SetUtils() {
        // Not to be used
    }

    public static List<String> getAvailableCardSets() {
        URL setsFolderURL = SetUtils.class.getResource("/sets");
        if (setsFolderURL == null) {
            throw new RuntimeException("Sets folder not found");
        }

        File[] files = new File(setsFolderURL.getPath()).listFiles();
        if (files == null) {
            throw new RuntimeException("No card sets available");
        }

        return Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".json"))
                .map(file -> file.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }

    public static List<Integer> getCardSetContent(String setName) {
        List<Card> fileCards = loadCardsFromConfig(setName);

        return fileCards.stream().map(Card::getId).collect(Collectors.toList());
    }

    public static List<CardInstance> getCardsFromConfig(String setName) {
        List<Card> fileCards = loadCardsFromConfig(setName);

        // Transform the List of Card into a List of CardInstance
        List<CardInstance> cards = new ArrayList<>();
        for (Card fileCard : fileCards) {
            fileCard.setSetName(setName);
            cards.add(new CardInstance(fileCard));

            if (!fileCard.isUnique()) {
                cards.add(new CardInstance(fileCard));
            }
        }

        return cards;
    }

    private static List<Card> loadCardsFromConfig(String setName) {
        // Open an input stream for the given configuration file
        try (InputStream is = Game.class.getClassLoader().getResourceAsStream("sets/" + setName + ".json")) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found : " + "sets/" + setName + ".json");
            }

            // Unmarshal file content into a list of Card
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            return objectMapper.readValue(is, typeFactory.constructCollectionType(List.class, Card.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
