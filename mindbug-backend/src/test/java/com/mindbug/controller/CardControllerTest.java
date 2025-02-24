// package com.mindbug.controller;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.mindbug.models.Card;

// import java.io.IOException;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// public class CardControllerTest {

//     private CardController cardController;

//     @BeforeEach
//     public void setUp() throws IOException {
//         cardController = new CardController();
//     }

//     @Test
//     public void testGetCardsBySet_FirstContact() throws IOException {
//         List<Card> cards = cardController.getCardsBySet("First_Contact");

//         assertNotNull(cards, "The card list should not be null");

//         assertEquals(32, cards.size(), "The number of cards in the First_Contact set is incorrect");
//     }
// }