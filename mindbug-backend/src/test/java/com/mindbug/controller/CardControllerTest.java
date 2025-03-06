package com.mindbug.controller;

import com.mindbug.models.Card;
import com.mindbug.services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CardControllerTest {

    private CardController cardController;
    private CardService cardService;

    @BeforeEach
    public void setUp() throws IOException {
        cardService = new CardService();
        cardController = new CardController(cardService);
    }

    @Test
    public void testGetCardsBySet_FirstContact() throws IOException {
        List<Card> cards = cardController.getCardsBySet("First_Contact");

        assertNotNull(cards, "The card list should not be null");

        assertEquals(32, cards.size(), "The number of cards in the First_Contact set is incorrect");
    }
}
