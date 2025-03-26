package com.mindbug.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
public class Card {

    @Id
    private Long id;

    private String name;
    private int copies;
    private int power;
    private Set<Keyword> keywords;
    private Set<Effect> effect;
}
