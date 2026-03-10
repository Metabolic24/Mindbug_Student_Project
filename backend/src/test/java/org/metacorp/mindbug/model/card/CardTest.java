package org.metacorp.mindbug.model.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardTest {
    @Test
    public void testCardUnmarshalling() {
        try (InputStream is = Card.class.getClassLoader().getResourceAsStream("sets/first_contact.json")) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Card> fileCards = objectMapper.readValue(is, typeFactory.constructCollectionType(List.class, Card.class));
            assertNotNull(fileCards);
            assertEquals(44, fileCards.size());

            Card exampleCard = fileCards.stream().filter(card -> card.getId() == 27).findFirst().orElse(null);
            assertNotNull(exampleCard);
            assertEquals("SPIDER OWL", exampleCard.getName());
            assertEquals(3, exampleCard.getPower());
            assertTrue(exampleCard.getEffects().isEmpty());
            assertNotNull(exampleCard.getKeywords());
            assertTrue(exampleCard.getKeywords().contains(CardKeyword.SNEAKY));
            assertTrue(exampleCard.getKeywords().contains(CardKeyword.POISONOUS));

            Card otherCard = fileCards.stream().filter(card -> card.getId() == 32).findFirst().orElse(null);
            assertNotNull(otherCard);
            assertEquals("URCHIN HURLER", otherCard.getName());
            assertEquals(5, otherCard.getPower());
            assertNotNull(otherCard.getKeywords());
            assertTrue(otherCard.getKeywords().contains(CardKeyword.HUNTER));
            assertEquals(1, otherCard.getEffects().size());
            assertTrue(otherCard.getEffects().containsKey(EffectTiming.PASSIVE));
            assertEquals(1, otherCard.getEffects().get(EffectTiming.PASSIVE).size());

            PowerUpEffect effect = (PowerUpEffect) otherCard.getEffects().get(EffectTiming.PASSIVE).getFirst();
            assertNotNull(effect);
            assertEquals(EffectType.POWER_UP, effect.getType());
            assertEquals(2, effect.getValue());
            assertNull(effect.getLifePoints());
            assertTrue(effect.isAllies());
            assertFalse(effect.isAlone());
            assertFalse(effect.isSelf());
            assertFalse(effect.isForEachAlly());
            assertTrue(effect.isSelfTurn());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
