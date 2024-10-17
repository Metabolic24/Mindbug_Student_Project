package org.metacorp.mindbug;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    public void testCardUnmarshalling() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        try (InputStream is = Card.class.getClassLoader().getResourceAsStream("default.json")) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found");
            }

            List<Card> fileCards = objectMapper.readValue(is, typeFactory.constructCollectionType(List.class, Card.class));
            assertNotNull(fileCards);
            assertEquals(44, fileCards.size());

            Card firstCard = fileCards.getFirst();
            assertNotNull(firstCard);
            assertEquals("Arachnibou", firstCard.getName());
            assertEquals(3, firstCard.getPower());
            assertTrue(firstCard.getEffects().isEmpty());
            assertNotNull(firstCard.getKeywords());
            assertTrue(firstCard.getKeywords().contains(Keyword.SNEAKY));
            assertTrue(firstCard.getKeywords().contains(Keyword.POISONOUS));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
