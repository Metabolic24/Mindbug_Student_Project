package com.mindbug.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_player_id")
    private Player currentPlayer;

    @OneToOne
    @JoinColumn(name = "player1_id", referencedColumnName = "id")
    private Player player1;

    @OneToOne
    @JoinColumn(name = "player2_id", referencedColumnName = "id")
    private Player player2;
}
