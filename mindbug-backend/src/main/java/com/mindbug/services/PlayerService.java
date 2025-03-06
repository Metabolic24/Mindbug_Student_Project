package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.repositories.PlayerRepository;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Player createPlayer(Player player) {
        return this.playerRepository.save(player);
    }

    public GameSessionCard getHandCard(Player player, Long  sessionCardId) {
        for  (GameSessionCard sessionCard : player.getHand()) {
            if (sessionCard.getId().equals(sessionCardId)) {
                return sessionCard;  // Retourne la carte si l'ID correspond
            }
        }
        return null;
    }

    public void fillPlayerHand(Player player) {
        // Liste des noms de cartes à ajouter
        List<String> handCards = List.of(
            "Bee_Bear.jpg",
            "Killer_Bee.jpg",
            "Gorillion.jpg", //pour l'instant ajouté a la main
            "Lone_Yeti.jpg",
            "Shark_Dog.jpg"
        );

        // Ajouter les cartes à la main du joueur
        for (String cardName : handCards) {
            GameSessionCard card = new GameSessionCard(null, cardName);
            player.getHand().add(card);  // Ajout de la carte à la main
        }
    }
}
