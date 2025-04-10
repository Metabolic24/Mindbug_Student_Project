package com.mindbug.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
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

    @Column
    private int mindbug;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSessionCard> hand = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSessionCard> drawPile = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSessionCard> battlefield = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSessionCard> discardPile = new ArrayList<>();

    public Player(String nickname) {
        this.nickname = nickname;
        // TODO: get lifepoints and mindbug from game configs
        this.lifepoints = 4;
        this.mindbug = 2;
    }

    public GameSessionCard drawCard() {
        if (this.drawPile.isEmpty()) {
            return null;
        }
        GameSessionCard card = this.drawPile.remove(0);
        this.hand.add(card);
        return card;
    }
}
