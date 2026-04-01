package org.metacorp.mindbug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.jvnet.hk2.annotations.Service;
import org.metacorp.mindbug.dto.card.LightCardDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.mapper.CardMapper;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility for card sets
 */
@Service
public class CardSetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardSetService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final CollectionType COLLECTION_TYPE = MAPPER.getTypeFactory().constructCollectionType(List.class, Card.class);

    /**
     * A map to store default card sets by name
     */
    private final Map<String, List<Card>> defaultCardSets = new HashMap<>();

    /**
     * A map to store card sets by name
     */
    private final Map<String, List<Card>> customCardSets = new HashMap<>();

    /**
     * A map to store cards by id
     */
    private final Map<Integer, Card> cards = new HashMap<>();

    /**
     * Constructor
     */
    public CardSetService() {
        try {
            loadCardSets();
        } catch (CardSetException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Load all the stored card sets
     *
     * @throws CardSetException if an error occurs while loading cards from set files
     */
    private void loadCardSets() throws CardSetException {
        URL setsFolderURL = CardSetService.class.getResource("/sets");
        if (setsFolderURL == null) {
            throw new CardSetException("Unable to locate set folder 'sets'");
        }

        File[] files = new File(setsFolderURL.getPath()).listFiles();
        if (files == null) {
            throw new CardSetException("Unable to locate any card set in folder " + setsFolderURL.getPath());
        }

        Set<File> cardSetFiles = Arrays.stream(files).filter(file -> file.getName().endsWith(".json")).collect(Collectors.toSet());
        for (File cardSetFile : cardSetFiles) {
            loadCardsFromConfig(cardSetFile);
        }
    }

    /**
     * @param cardSetFile the card set file
     * @throws CardSetException if an error occurs while loading cards from the file
     */
    private void loadCardsFromConfig(File cardSetFile) throws CardSetException {
        try {
            String cardSetName = cardSetFile.getName().replace(".json", "");
            List<Card> cardsSet = MAPPER.readValue(cardSetFile, COLLECTION_TYPE);

            defaultCardSets.put(cardSetName, cardsSet);
            cardsSet.forEach(card -> {
                card.setSetName(cardSetName);
                cards.putIfAbsent(card.getId(), card);
            });
        } catch (IOException e) {
            String errorMessage = MessageFormat.format("Unable to load cards from file {0}", cardSetFile.getName());
            LOGGER.error(errorMessage, e);
            throw new CardSetException(e);
        }
    }

    /**
     * @return the name of the available card sets
     */
    public List<String> getAvailableSets() {
        List<String> availableSets = new ArrayList<>(defaultCardSets.keySet().stream().sorted(Comparator.naturalOrder()).toList());
        availableSets.addAll(customCardSets.keySet().stream().sorted(Comparator.naturalOrder()).toList());
        return availableSets;
    }

    /**
     * @param cardSetName the card set name
     * @return the list of card IDs contained in the given set if it exists, null otherwise
     */
    public List<LightCardDTO> getCardSetContent(String cardSetName) {
        List<Card> setCards = defaultCardSets.get(cardSetName);
        if (setCards == null) {
            setCards = customCardSets.get(cardSetName);
        }

        return setCards == null ? null
                : setCards.stream().map(CardMapper::fromCard)
                  .sorted(Comparator.comparing(LightCardDTO::getId))
                  .collect(Collectors.toList());
    }

    /**
     * @param cardSetName the card set name
     * @return the list of CardInstance corresponding to the given set name
     * @throws CardSetException if an error occurs while looking for the card set
     */
    public List<CardInstance> getCardInstances(String cardSetName) throws CardSetException {
        List<Card> setCards = defaultCardSets.get(cardSetName);
        if (setCards == null) {
            setCards = customCardSets.get(cardSetName);
        }

        if (setCards == null) {
            String errorMessage = MessageFormat.format("{0} card set not found", cardSetName);
            LOGGER.error(errorMessage);
            throw new CardSetException(errorMessage);
        }

        // Transform the List of Card into a List of CardInstance
        List<CardInstance> cardInstances = new ArrayList<>();
        for (Card card : setCards) {
            // Duplicate the card to avoid unexpected behavior due to shared card objects
            Card copyCard = new Card(card);
            copyCard.setSetName(cardSetName);

            cardInstances.add(new CardInstance(copyCard));

            if (!card.isUnique()) {
                cardInstances.add(new CardInstance(card));
            }
        }

        return cardInstances;
    }

    /**
     *
     * @param cardSetName the card set name
     * @param cardsIds    the cards IDs
     * @throws CardSetException if an error occurs while looking for a card or if there are too much copies of a card
     */
    public void create(String cardSetName, List<Integer> cardsIds) throws CardSetException {
        Map<Integer, Card> cardsSet = new HashMap<>();

        for (Integer cardId : cardsIds) {
            Card card = cardsSet.get(cardId);
            if (card == null) {
                card = cards.get(cardId);
                if (card == null) {
                    throw new CardSetException("Unable to find card with id " + cardId);
                } else {
                    // Copy the card for the current set and update the cards set map
                    card = new Card(card);
                    card.setSetName(cardSetName);
                    card.setUnique(true); // Set unique to true by default (may be overridden later)
                    cardsSet.put(cardId, card);
                }
            } else if (card.isUnique()) {
                card.setUnique(false);
            } else {
                throw new CardSetException(MessageFormat.format("Too much copies of {0} in set {1}", card.getName(), cardSetName));
            }
        }

        customCardSets.put(cardSetName, new ArrayList<>(cardsSet.values()));
    }

    /**
     * Export a card set
     *
     * @param cardSetName the card set name
     */
    public void export(String cardSetName) throws CardSetException {
        List<Card> setCards = customCardSets.get(cardSetName);
        if (setCards == null) {
            throw new CardSetException(cardSetName + " card set not found");
        }

        try {
            Files.createDirectories(Path.of("sets"));

            File exportedSet = Path.of("sets", cardSetName + ".json").toFile();
            if (exportedSet.exists()) {
                throw new CardSetException(cardSetName + " set file already exists");
            } else {
                MAPPER.writerWithDefaultPrettyPrinter().writeValue(exportedSet, setCards);
            }
        } catch (IOException e) {
            throw new CardSetException("Unable to export card set " + cardSetName, e);
        }
    }

    /**
     * @return all the available cards from any card set
     */
    public List<LightCardDTO> getAllCards() {
        return cards.values().stream().map(CardMapper::fromCard)
                .sorted(Comparator.comparing(LightCardDTO::getId))
                .collect(Collectors.toList());
    }
}
