package com.mindbug.models;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "current_player_id", referencedColumnName = "id")
    private Player currentPlayer;

    @OneToOne
    @JoinColumn(name = "player1_id", referencedColumnName = "id")
    private Player player1;

    @OneToOne
    @JoinColumn(name = "player2_id", referencedColumnName = "id")
    private Player player2;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public Player getPlayer1() {
        return player1;
    }
    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
