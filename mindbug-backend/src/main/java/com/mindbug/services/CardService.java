package com.mindbug.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.repositories.CardRepository;
import com.mindbug.repositories.GameSessionCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private GameSessionCardRepository gameSessionCardRepository;

    private Set<GameSessionCard> allCards;

    private List<Card> cards;



    public CardService() throws IOException {
        cards = loadCardsFromSet("First_Contact");
        if (cards.isEmpty()) {
            System.out.println("Failed to load default cards.");
        }
    }

    public List<Card> getCardsBySet(String set) throws IOException {
        return loadCardsFromSet(set);
    }

    public List<Card> getAllCards() {
        return cards;
    }

    public List<String> getAvailableSets() {
        URL resourceUrl = getClass().getResource("/sets");
        if (resourceUrl == null) {
            throw new RuntimeException("Sets folder not found!");
        }
        File folder = new File(resourceUrl.getPath());
        File[] files = folder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".json"))
                .map(file -> file.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }

    private List<Card> loadCardsFromSet(String setName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sets/" + setName + ".json");
        if (is == null) {
            throw new IOException(setName + ".json file not found!");
        }
        return mapper.readValue(is, new TypeReference<List<Card>>() {
        });
    }

    public void distributeCards(Game game) {
        try {
            List<Card> rawCards = getCardsBySet("First_Contact");

            List<GameSessionCard> gameSessionCards = expandCardCopies(rawCards);

            Collections.shuffle(gameSessionCards);

            if (gameSessionCards.size() < 20) {
                throw new IllegalStateException("Not enough cards to start the game!");
            }

            List<GameSessionCard> player1Cards = new ArrayList<>(gameSessionCards.subList(0, 10));
            List<GameSessionCard> player2Cards = new ArrayList<>(gameSessionCards.subList(10, 20));

            gameSessionCards.subList(0, 20).clear();

            distributeCardsToPlayer(game.getPlayer1(), player1Cards);
            distributeCardsToPlayer(game.getPlayer2(), player2Cards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<GameSessionCard> expandCardCopies(List<Card> rawCards) {
        List<GameSessionCard> expandedCards = new ArrayList<>();
        Set<String> addedCards = new HashSet<>();

        for (Card card : rawCards) {
            if (!addedCards.contains(card.getName())) {
                addedCards.add(card.getName());

                Card savedCard = cardRepository.save(card);

                for (int i = 0; i < savedCard.getCopies(); i++) {
                    GameSessionCard gameSessionCard = new GameSessionCard(savedCard);
                    expandedCards.add(gameSessionCardRepository.save(gameSessionCard));
                }
            }
        }
        return expandedCards;
    }

    private void distributeCardsToPlayer(Player player, List<GameSessionCard> availableCards) {
        List<GameSessionCard> hand = new ArrayList<>(availableCards.subList(0, 5));
        List<GameSessionCard> drawPile = new ArrayList<>(availableCards.subList(5, 10));

        player.setHand(hand);
        player.setDrawPile(drawPile);
    }

}
