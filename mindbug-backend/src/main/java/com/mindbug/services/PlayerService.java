package com.mindbug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindbug.models.Player;
import com.mindbug.repositories.PlayerRepository;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Player createPlayer(Player player) {
        return this.playerRepository.save(player);
    }
    
}
