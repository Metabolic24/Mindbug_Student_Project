package com.mindbug.controller;

import com.mindbug.models.Card;
import com.mindbug.services.CardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{set}")
    public List<Card> getCardsBySet(@PathVariable String set) throws IOException {
        return cardService.getCardsBySet(set);
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/sets")
    public List<String> getAvailableSets() {
        return cardService.getAvailableSets();
    }
}
