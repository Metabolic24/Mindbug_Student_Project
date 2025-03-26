package com.mindbug.controller;

import com.mindbug.models.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CardControllerTest {

    @Autowired
    private CardController cardController;

    @Test
    public void testGetCardsBySet_FirstContact() throws IOException {
        List<Card> cards = cardController.getCardsBySet("First_Contact");

        assertNotNull(cards, "The card list should not be null");

        assertEquals(32, cards.size(), "The number of cards in the First_Contact set is incorrect");
    }
}
