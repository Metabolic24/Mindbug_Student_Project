//package com.mindbug.controller;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mindbug.models.Card;
//import com.mindbug.models.Player;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/cards")
//public class CardController {
//
//    private List<Card> originalCards;
//    private static List<Card> cards;
//    private Player player1;
//    private Player player2;
//    private Map<String, Integer> cardCopiesMap;
//
//    public CardController() throws IOException {
//        // Charger un fichier par défaut
//        ObjectMapper mapper = new ObjectMapper();
//        InputStream is = getClass().getResourceAsStream("/sets/First_Contact.json");
//        if (is == null) {
//            System.out.println("cards.json file not found!");
//        } else {
//            System.out.println("Successfully loaded cards.json file.");
//        }
//        originalCards = mapper.readValue(is, new TypeReference<List<Card>>() {});
//        cards = new ArrayList<>(originalCards);
//        //cardCopiesMap = new HashMap<>();
//        //initializeCardCopiesMap();
//        System.out.println(originalCards);
//
//    }
//    public static List<Card> getListCard(){
//        return cards;
//    }
//
////    private void initializeCardCopiesMap() {
////        for (Card card : cards) {
////            cardCopiesMap.put(card.getName(), card.getCopies());
////        }
////    }
////
////    private boolean checkCardCopies(String cardName) {
////        if (cardCopiesMap.containsKey(cardName)) {
////            int copies = cardCopiesMap.get(cardName);
////            if (copies > 0) {
////                cardCopiesMap.put(cardName, copies - 1);
////                return false;
////            }
////        }
////        return true;
////    }
//
//    @GetMapping("/{set}")
//    public List<Card> getCardsBySet(@PathVariable String set) throws IOException {
//        // Charger le fichier du set sélectionné
//        ObjectMapper mapper = new ObjectMapper();
//        InputStream is = getClass().getResourceAsStream("/sets/" + set + ".json");
//        if (is != null) {
//            return mapper.readValue(is, new TypeReference<List<Card>>() {});
//        }
//        return new ArrayList<>(); // Retourne une liste vide si le set n'existe pas
//    }
//
//    @GetMapping
//    public List<Card> getAllCards() {
//        return cards;
//    }
//
//    @GetMapping("/sets")
//    public List<String> getAvailableSets() {
//        // Récupérer la liste des fichiers .json dans le dossier /sets
//        File folder = new File(getClass().getResource("/sets").getFile());
//        return Arrays.stream(folder.listFiles())
//                .filter(file -> file.getName().endsWith(".json"))
//                .map(file -> file.getName().replace(".json", ""))
//                .collect(Collectors.toList());
//    }
//
//    private void resetCards() {
//        cards = new ArrayList<>(originalCards);
//    }
//
////    @GetMapping("/initialize")
////    public String initializeGame() {
////        resetCards();
////        if (cards.size() < 20) {
////            return "Not enough cards to initialize the game";
////        }
////        Collections.shuffle(cards);
////        // Each player draws 10 random cards from the deck to form their draw pile
////        player1.clear();
////        player2.clear();
////        Random random = new Random();
////
////        while (player1.getTotalCards().size() < 10) {
////            Card card = cards.get(random.nextInt(cards.size()));
////            if (checkCardCopies(card.getName())) {
////                player1.getTotalCards().add(card);
////                cards.remove(card);
////            }
////        }
////
////        while (player2.getTotalCards().size() < 10) {
////            Card card = cards.get(random.nextInt(cards.size()));
////            if (checkCardCopies(card.getName())) {
////                player2.getTotalCards().add(card);
////                cards.remove(card);
////            }
////        }
////        // Each player draws 5 cards from their draw pile to form their starting hand
////        player1.getHand().addAll(player1.getTotalCards().subList(0, 5));
////        player2.getHand().addAll(player2.getTotalCards().subList(0, 5));
////        player1.getDrawPile().addAll(player1.getTotalCards().subList(5, 10));
////        player2.getDrawPile().addAll(player2.getTotalCards().subList(5, 10));
////
////        return "Game initialized with 5 cards in hand and 5 cards in draw pile for each player";
////    }
////
////    @GetMapping("/hand/{player}")
////    public List<Card> getPlayerHand(@PathVariable int player) {
////        return player == 1 ? player1.getHand() : player2.getHand();
////    }
////
////    @GetMapping("/drawPile/{player}")
////    public List<Card> getDrawPile(@PathVariable int player) {
////        return player == 1 ? player1.getDrawPile() : player2.getDrawPile();
////    }
//}
