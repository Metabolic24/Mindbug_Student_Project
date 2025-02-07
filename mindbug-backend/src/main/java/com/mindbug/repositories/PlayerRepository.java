package com.mindbug.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindbug.models.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    
}