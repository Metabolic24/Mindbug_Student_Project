package com.mindbug.services;

import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CardService cardService;

    public Game createGame(Game game) {
        return this.gameRepository.save(game);
    }

    public void distributeCards(Game game) {
        try {
            List<Card> rawCards = cardService.getCardsBySet("First_Contact");
            List<GameSessionCard> gameSessionCards = expandCardCopies(rawCards);
            Collections.shuffle(gameSessionCards);

            if (gameSessionCards.size() < 20) {
                throw new IllegalStateException("Not enough cards to start the game!");
            }

            List<GameSessionCard> player1Cards = new ArrayList<>(gameSessionCards.subList(0, 10));
            List<GameSessionCard> player2Cards = new ArrayList<>(gameSessionCards.subList(10, 20));

            distributeCardsToPlayer(game.getPlayer1(), player1Cards);
            distributeCardsToPlayer(game.getPlayer2(), player2Cards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<GameSessionCard> expandCardCopies(List<Card> rawCards) {
        List<GameSessionCard> expandedCards = new ArrayList<>();
        for (Card card : rawCards) {
            for (int i = 0; i < card.getCopies(); i++) {
                expandedCards.add(new GameSessionCard(card));
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
