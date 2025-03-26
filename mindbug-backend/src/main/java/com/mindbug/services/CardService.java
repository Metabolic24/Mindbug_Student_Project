package com.mindbug.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.exception.ResourceNotFoundException;
import com.mindbug.exception.ResourceLoadingException;
import com.mindbug.models.Card;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final List<Card> cards;

    public CardService() throws IOException {
        cards = loadCardsFromSet("First_Contact");
        if (cards.isEmpty()) {
            throw new IllegalStateException("Failed to load default cards.");
        }
    }

    public List<Card> getAllCards() {
        return cards;
    }

    public List<String> getAvailableSets() { 
        URL resourceUrl = getClass().getResource("/sets");
        if (resourceUrl == null) {
            throw new ResourceNotFoundException("Sets folder not found!");
        }
        File folder = new File(resourceUrl.getPath());
        File[] files = folder.listFiles();
        if (files == null) {
            throw new ResourceLoadingException("Failed to load files from sets folder.");
        }
        return Arrays.stream(files)
            .filter(file -> file.getName().endsWith(".json"))
            .map(file -> file.getName().replace(".json", ""))
            .collect(Collectors.toList()); 
    }

    public List<Card> loadCardsFromSet(String setName) throws IOException { 
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sets/" + setName + ".json");
        if (is == null) {
            throw new IOException(setName + ".json file not found!");
        }
        return mapper.readValue(is, new TypeReference<List<Card>>() { }); 
    }
}
