package com.mindbug.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.models.Card;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

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
        return mapper.readValue(is, new TypeReference<List<Card>>() { }); 
    }
}
