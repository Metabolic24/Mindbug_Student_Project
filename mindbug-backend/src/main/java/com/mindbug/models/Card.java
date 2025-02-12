package com.mindbug.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@Entity
public class Card {
    //private int number;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "game_session_card_id_id")
    private GameSessionCard gameSessionCardId;
    private String name;
    private int copies;
    private int power;
    @ElementCollection
    private List<String> keywords;
    private String trigger_condition;
    @ElementCollection
    private List<EffectType> effect_type;

    public Card() {
    }

    public Card(Card card) {
        this.gameSessionCardId = card.gameSessionCardId;
        this.name = card.name;
        this.copies = card.copies;
        this.power = card.power;
        this.keywords = card.keywords;
        this.trigger_condition = card.trigger_condition;
        this.effect_type = card.effect_type;
    }

    public GameSessionCard getGameSessionCardId() {
        return gameSessionCardId;
    }

    public void setGameSessionCardId(GameSessionCard gameSessionCardId) {
        this.gameSessionCardId = gameSessionCardId;
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

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getTrigger_condition() {
        return trigger_condition;
    }

    public void setTrigger_condition(String triggerCondition) {
        this.trigger_condition = triggerCondition;
    }

    public List<EffectType> getEffect_type() {
        return effect_type;
    }

    public void setEffect_type(List<EffectType> effectTypes) {
        this.effect_type = effectTypes;
    }
    @Override
    public String toString() {
        return "Card{" +
                "gameSessionCardId=" + gameSessionCardId +
                ", name='" + name + '\'' +
                ", copies=" + copies +
                ", power=" + power +
                ", keywords=" + keywords +
                ", trigger_condition='" + trigger_condition + '\'' +
                ", effect_type=" + effect_type +
                '}';
    }
}

