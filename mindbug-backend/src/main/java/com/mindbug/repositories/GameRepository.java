package com.mindbug.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindbug.models.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

}
