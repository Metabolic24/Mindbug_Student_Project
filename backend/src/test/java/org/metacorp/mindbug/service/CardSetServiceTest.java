package org.metacorp.mindbug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.dto.card.LightCardDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardSetServiceTest {

    private final CardSetService cardSetService = new CardSetService();
    private final String cardSetName = "test_set";

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("sets", cardSetName + ".json"));
    }

    @Test
    public void getAvailableSets() {
        List<String> availableCardSets = cardSetService.getAvailableSets();
        assertNotNull(availableCardSets);
        assertEquals(2, availableCardSets.size());
        assertTrue(availableCardSets.contains("first_contact"));
        assertTrue(availableCardSets.contains("beyond_evolution"));
    }

    @Test
    public void getCardSetContent_firstContact() {
        List<LightCardDTO> cardSetContent = cardSetService.getCardSetContent("first_contact");
        assertNotNull(cardSetContent);
        assertEquals(44, cardSetContent.size());

        for (int cardId = 1; cardId <= cardSetContent.size(); cardId++) {
            assertEquals(cardId, cardSetContent.get(cardId - 1).getId());
        }
    }

    @Test
    public void getCardSetContent_beyondEvolution() {
        List<LightCardDTO> cardSetContent = cardSetService.getCardSetContent("beyond_evolution");
        assertNotNull(cardSetContent);
        assertEquals(38, cardSetContent.size());

        for (int cardId = 45; cardId < 45 + cardSetContent.size(); cardId++) {
            assertEquals(cardId, cardSetContent.get(cardId - 45).getId());
        }
    }

    @Test
    public void getCardSetContent_badSetName() {
        List<LightCardDTO> cardSetContent = cardSetService.getCardSetContent("bad_set_name");
        assertNull(cardSetContent);
    }

    @Test
    public void getCardInstances_firstContact() throws CardSetException {
        List<CardInstance> cardInstances = cardSetService.getCardInstances("first_contact");
        assertNotNull(cardInstances);
        assertEquals(72, cardInstances.size());

        checkCardInstances(cardInstances);
    }

    @Test
    public void getCardInstances_beyond_evolution() throws CardSetException {
        List<CardInstance> cardInstances = cardSetService.getCardInstances("beyond_evolution");
        assertNotNull(cardInstances);
        assertEquals(53, cardInstances.size());

        checkCardInstances(cardInstances);
    }

    @Test
    public void getCardInstances_badSetName() {
        CardSetException exception = assertThrows(CardSetException.class, () -> cardSetService.getCardInstances("bad_set_name"));
        assertEquals("bad_set_name card set not found", exception.getMessage());
    }

    @Test
    public void create_nominal() throws CardSetException {
        cardSetService.create("test_set", Arrays.asList(1, 13, 15, 18));

        List<String> availableCardSets = cardSetService.getAvailableSets();
        assertEquals(3, availableCardSets.size());
        assertTrue(availableCardSets.contains("test_set"));
    }

    @Test
    public void create_cardNotFound() {
        CardSetException exception = assertThrows(CardSetException.class, () -> cardSetService.create(cardSetName, Arrays.asList(1, 13, -15, 18)));
        assertEquals("Unable to find card with id -15", exception.getMessage());
    }

    @Test
    public void create_threeCopies() {
        CardSetException exception = assertThrows(CardSetException.class, () -> cardSetService.create(cardSetName, Arrays.asList(1, 13, 1, 15, 18, 1)));
        assertEquals("Too much copies of AXOLOTL HEALER in set " + cardSetName, exception.getMessage());
    }

    @Test
    public void export_nominal() throws CardSetException, IOException {
        cardSetService.create(cardSetName, Arrays.asList(1, 13, 15, 1, 18));
        cardSetService.export(cardSetName);

        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Card.class);

        File actualFile = Path.of("sets", cardSetName + ".json").toFile();

        URL setsFolderURL = CardSetService.class.getResource("/expected_sets/" + cardSetName + ".json");
        assert setsFolderURL != null;
        File expectedFile = new File(setsFolderURL.getPath());

        List<Card> actualCards = mapper.readValue(actualFile, collectionType);
        List<Card> expectedCards = mapper.readValue(expectedFile, collectionType);

        assertEquals(actualCards.size(), expectedCards.size());
        assertTrue(expectedCards.containsAll(actualCards));
    }

    @Test
    public void export_cardSetNotFound() {
        CardSetException exception = assertThrows(CardSetException.class, () -> cardSetService.export(cardSetName));
        assertEquals(cardSetName + " card set not found", exception.getMessage());
    }

    @Test
    public void export_alreadyExists() throws CardSetException {
        cardSetService.create(cardSetName, Arrays.asList(4, 17, 23, 32, 8, 6, 21, 18));
        cardSetService.export(cardSetName);
        CardSetException exception = assertThrows(CardSetException.class, () -> cardSetService.export(cardSetName));
        assertEquals(cardSetName + " set file already exists", exception.getMessage());
    }

    private void checkCardInstances(List<CardInstance> cardInstances) {
        List<Card> foundCards = new ArrayList<>();
        List<Card> duplicatedCards = new ArrayList<>();
        for (CardInstance cardInstance : cardInstances) {
            assertFalse(duplicatedCards.contains(cardInstance.getCard()));

            if (foundCards.contains(cardInstance.getCard())) {
                assertFalse(cardInstance.getCard().isUnique());

                foundCards.remove(cardInstance.getCard());
                duplicatedCards.add(cardInstance.getCard());
            } else {
                foundCards.add(cardInstance.getCard());
            }
        }

        for (Card cardInstance : foundCards) {
            assertTrue(cardInstance.isUnique());
        }
    }
}
