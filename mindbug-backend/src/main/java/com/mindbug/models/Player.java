package com.mindbug.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mindbug.services.GameSession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column
    private int lifepoints;

    @OneToMany
    @JoinColumn(name = "hand_id")
    private List<GameSessionCard> hand = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "board_id")
    private List<GameSessionCard> board = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "discard_pile_id")
    private List<GameSessionCard> discardPile = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "draw_pile_id")
    private List<GameSessionCard> drawPile = new ArrayList<>();

    @Column
    private int mindbug;

    public Player(String nickname) {
        this.nickname = nickname;
        // TODO: get lifepoints and mindbug from game configs
        this.lifepoints = 4;
        this.mindbug = 2;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getLifepoints() {
        return lifepoints;
    }

    public void setLifepoints(int lifepoints) {
        this.lifepoints = lifepoints;
    }

    public List<GameSessionCard> getHand() {
        return hand;
    }

    public void setHand(List<GameSessionCard> hand) {
        this.hand = hand;
    }

    public List<GameSessionCard> getBoard() {
        return board;
    }

    public void setBoard(List<GameSessionCard> board) {
        this.board = board;
    }

    public List<GameSessionCard> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(List<GameSessionCard> discardPile) {
        this.discardPile = discardPile;
    }

    public List<GameSessionCard> getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(List<GameSessionCard> drawPile) {
        this.drawPile = drawPile;
    }
}