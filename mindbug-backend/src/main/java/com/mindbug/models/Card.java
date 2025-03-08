package com.mindbug.models;

import java.util.Set;


public class Card {
    private String name;
    private int copies;
    private int power;
    private Set<Keyword> keywords;
    private Set<Effect> effect;

    public Card() {
    }

    public Card(Card card) {
        this.name = card.getName();
        this.copies = card.getCopies();
        this.power = card.getPower();
        this.keywords = card.getKeywords();
        this.effect = card.getEffect();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public Set<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }


    public Set<Effect> getEffect() {
        return effect;
    }

    public void setEffect(Set<Effect> effect) {
        this.effect = effect;
    }
}

