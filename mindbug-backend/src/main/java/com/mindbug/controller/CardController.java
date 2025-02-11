package com.mindbug.controller;

import com.mindbug.model.Card;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private List<Card> cards;

    public CardController() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/cards.json");
        if (is == null) {
        System.out.println("cards.json file not found!");
        } else {
            System.out.println("Successfully loaded cards.json file.");
        }
        cards = mapper.readValue(is, new TypeReference<List<Card>>() { });
    }

    @GetMapping("/{set}")
    public List<Card> getCardsBySet(@PathVariable String set) {
        return cards.stream()
                .filter(card -> card.getSet().equalsIgnoreCase(set))
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cards;
    }
}
