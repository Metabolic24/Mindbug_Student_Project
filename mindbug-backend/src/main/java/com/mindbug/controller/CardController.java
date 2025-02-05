package com.mindbug.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.model.Card;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private List<Card> cards;

    public CardController() throws IOException {
        // Charger un fichier par défaut
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sets/First_Contact.json");
        if (is == null) {
            System.out.println("cards.json file not found!");
        } else {
            System.out.println("Successfully loaded cards.json file.");
        }
        cards = mapper.readValue(is, new TypeReference<List<Card>>() {});
    }

    @GetMapping("/{set}")
    public List<Card> getCardsBySet(@PathVariable String set) throws IOException {
        // Charger le fichier du set sélectionné
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sets/" + set + ".json");
        if (is != null) {
            return mapper.readValue(is, new TypeReference<List<Card>>() {});
        }
        return new ArrayList<>(); // Retourne une liste vide si le set n'existe pas
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cards;
    }

    @GetMapping("/sets")
    public List<String> getAvailableSets() {
        // Récupérer la liste des fichiers .json dans le dossier /sets
        File folder = new File(getClass().getResource("/sets").getFile());
        return Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().endsWith(".json"))
                .map(file -> file.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }
}
