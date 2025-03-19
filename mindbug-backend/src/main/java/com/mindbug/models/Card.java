package com.mindbug.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Card {

    private String name;
    private int copies;
    private int power;
    private Set<Keyword> keywords;
    private Set<Effect> effect;
}
