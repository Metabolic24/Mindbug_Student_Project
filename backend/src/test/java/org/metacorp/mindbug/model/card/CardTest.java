package org.metacorp.mindbug.model.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.effect.PowerUpEffect;

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
        try (InputStream is = Card.class.getClassLoader().getResourceAsStream("first_contact.json")) {
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
            assertTrue(firstCard.getKeywords().contains(CardKeyword.SNEAKY));
            assertTrue(firstCard.getKeywords().contains(CardKeyword.POISONOUS));

            Card tenthCard = fileCards.get(10);
            assertNotNull(tenthCard);
            assertEquals("Oursins Hurleurs", tenthCard.getName());
            assertEquals(5, tenthCard.getPower());
            assertNotNull(tenthCard.getKeywords());
            assertTrue(tenthCard.getKeywords().contains(CardKeyword.HUNTER));
            assertEquals(1, tenthCard.getEffects().size());
            assertTrue(tenthCard.getEffects().containsKey(EffectTiming.PASSIVE));
            assertEquals(1, tenthCard.getEffects().get(EffectTiming.PASSIVE).size());

            PowerUpEffect effect = (PowerUpEffect) tenthCard.getEffects().get(EffectTiming.PASSIVE).getFirst();
            assertNotNull(effect);
            assertEquals(EffectType.POWER_UP, effect.getType());
            assertEquals(2, effect.getValue());
            assertNull(effect.getLifePoints());
            assertTrue(effect.isAllies());
            assertFalse(effect.isAlone());
            assertFalse(effect.isSelf());
            assertFalse(effect.isByEnemy());
            assertTrue(effect.isSelfTurn());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
