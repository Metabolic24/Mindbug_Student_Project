package com.mindbug.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Transient;

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

    @Column
    private int mindbug;

    @Transient
    private List<GameSessionCard> hand = new ArrayList<>();

    @Transient
    private List<GameSessionCard> drawPile = new ArrayList<>();

    private List<GameSessionCard> hand = new ArrayList<>();

    public Player(String nickname) {
        this.nickname = nickname;
        // TODO: get lifepoints and mindbug from game configs
        this.lifepoints = 4;
        this.mindbug = 2;
    }

}
