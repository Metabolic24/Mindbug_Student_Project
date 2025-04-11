package com.mindbug.repositories;

import com.mindbug.models.GameSessionCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionCardRepository extends JpaRepository<GameSessionCard, Long> {

}
