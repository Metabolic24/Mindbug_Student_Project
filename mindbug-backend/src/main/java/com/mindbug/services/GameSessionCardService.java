package com.mindbug.services;

import com.mindbug.models.GameSessionCard;
import com.mindbug.repositories.GameSessionCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSessionCardService {

    @Autowired
    private GameSessionCardRepository gameSessionCardRepository;

    public GameSessionCard createGameSessionCard(GameSessionCard gameSessionCard) {
        return this.gameSessionCardRepository.save(gameSessionCard);
    }
}
