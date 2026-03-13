package org.metacorp.mindbug.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.player.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for cards
 */
public class CardUtils {

    /**
     * @param cards the cards to analyze
     * @return the highest power in the given cards
     */
    public static int getHighestPower(Collection<CardInstance> cards) {
        return cards.stream().map(CardInstance::getPower)
                .reduce(0, (min, current) -> current > min ? current : min);
    }
     /**
     * @param cards    the cards to analyze
     * @param refPower the reference power
     * @return true if at least one card has a power lower than the reference value, false otherwise
     */
    public static boolean anyPowerLower(Collection<CardInstance> cards, int refPower) {
        return cards.stream().anyMatch(card -> card.getPower() < refPower);
    }
    /**
     * @param cards    the cards to analyze
     * @param refPower the reference power
     * @return true if at no card has a power higher than the reference value, false otherwise
     */
    public static boolean noPowerHigher(Collection<CardInstance> cards, int refPower) {
        return cards.stream().anyMatch(card -> card.getPower() > refPower);
    }

    public static List<String> getAvailableCardSets() {
        URL setsFolderURL = CardUtils.class.getResource("/sets");
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
    /**
     * @param cards    the cards to analyze
     * @param keyword  the keyword to filter on
     * @param refPower the reference power
     * @return true if at least one card has the given keyword and a power higher than the reference value, false otherwise
     */
    public static boolean anyKeywordWithHigherOrEqualPower(Collection<CardInstance> cards, CardKeyword keyword, int refPower) {
        return cards.stream().filter(card -> card.hasKeyword(keyword))
                .anyMatch(card -> card.getPower() >= refPower); 
    }
     /**
     * @param cards    the cards to analyze
     * @param keyword  the keyword to filter on
     * @param refPower the reference power
     * @return true if no card has the given keyword and a power higher than the reference value, false otherwise
     */
    public static boolean noKeywordWithHigherPower(Collection<CardInstance> cards, CardKeyword keyword, int refPower) {
        return cards.stream().filter(card -> card.hasKeyword(keyword))
                .noneMatch(card -> card.getPower() > refPower);
    }
    /**
     * @param cards    the cards to analyze
     * @param refPower the reference power
     * @return the cards having a power higher or equal to the reference value
     */
    public static List<CardInstance> getCardsWithHigherOrEqualPower(Collection<CardInstance> cards, int refPower) {
        return cards.stream().filter(card -> card.getPower() >= refPower).collect(Collectors.toList());
    }
    

    public static List<Integer> getCardSetContent(String setName) {
        List<Card> fileCards = loadCardsFromConfig(setName);

        return fileCards.stream().map(Card::getId).collect(Collectors.toList());
    }
     /**
     * @param cards    the cards to analyze
     * @param refPower the reference power
     * @return the cards having a power lower or equal to the reference value
     */
    public static List<CardInstance> getCardsWithLowerOrEqualPower(Collection<CardInstance> cards, int refPower) {
        return cards.stream().filter(card -> card.getPower() <= refPower).collect(Collectors.toList());
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
    /**
     * @param cards the cards to analyze
     * @return the list containing the card(s) having the highest power
     */
    public static List<CardInstance> getHighestCards(Collection<CardInstance> cards) {
        List<CardInstance> highestCards = new ArrayList<>();
        int highestPower = 1;

        for (CardInstance card : cards) {
            if (card.getPower() > highestPower) {
                highestPower = card.getPower();
                highestCards.clear();
                highestCards.add(card);
            } else if (card.getPower() == highestPower) {
                highestCards.add(card);
            }
        }

        return highestCards;
    }
     /**
     * @param cards the cards to analyze
     * @return the list containing the card(s) having the lowest power
     */
    public static List<CardInstance> getLowestCards(List<CardInstance> cards) {
        return getLowestCards(cards, Integer.MAX_VALUE);
    }
    /**
     * Get the cards with the lowest power on boards
     *
     * @param players the players concerned by the request
     * @return the list containing the lowest cards of any player
     */
    public static List<CardInstance> getLowestCards(Set<Player> players) {
        List<CardInstance> lowestCards = new ArrayList<>();
        int lowestPower = Integer.MAX_VALUE;

        for (Player player : players) {
            List<CardInstance> currentCards = getLowestCards(player.getBoard(), lowestPower);

            if (!currentCards.isEmpty()) {
                int currentPower = currentCards.getFirst().getPower();

                if (currentPower < lowestPower) {
                    lowestPower = currentPower;
                    lowestCards = new ArrayList<>(currentCards);
                } else {
                    // currentPower is equal to lowestPower, so we add all retrieved cards to the list
                    lowestCards.addAll(currentCards);
                }
            
            }
        }

        return lowestCards;
    }
    /**
     * @param cards       the cards to analyze
     * @param lowestPower the initial lowest power
     * @return the list containing the card(s) having the lowest power
     */
    private static List<CardInstance> getLowestCards(List<CardInstance> cards, int lowestPower) {
        List<CardInstance> lowestCards = new ArrayList<>();

        for (CardInstance card : cards) {
            if (card.getPower() < lowestPower) {
                lowestPower = card.getPower();
                lowestCards.clear();
                lowestCards.add(card);
            } else if (card.getPower() == lowestPower) {
                lowestCards.add(card);
            }
        }
         return lowestCards;
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
    /**
     * @param cards   the cards to analyze
     * @param keyword the keyword to filter on
     * @return true if at least one card has the given keyword, false otherwise
     */
    public static boolean hasKeyword(Collection<CardInstance> cards, CardKeyword keyword) {
        return cards.stream().anyMatch(card -> card.hasKeyword(keyword));
    }
    /**
     * @param cards   the cards to analyze
     * @param keyword the keyword to filter on
     * @return the cards having the given keyword
     */
    public static List<CardInstance> getKeywordCards(Collection<CardInstance> cards, CardKeyword keyword) {
        return cards.stream().filter(card -> card.hasKeyword(keyword)).collect(Collectors.toList());
    }

}
