package com.mindbug.models;

<<<<<<< HEAD
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
    private List<EffectType> effect;

    public Card() {
    }

    public Card(Card card) {
        this.gameSessionCardId = card.gameSessionCardId;
        this.name = card.name;
        this.copies = card.copies;
        this.power = card.power;
        this.keywords = card.keywords;
        this.trigger_condition = card.trigger_condition;
        this.effect = card.effect;
    }

    public GameSessionCard getGameSessionCardId() {
        return gameSessionCardId;
    }

    public void setGameSessionCardId(GameSessionCard gameSessionCardId) {
        this.gameSessionCardId = gameSessionCardId;
    }
=======
import java.util.Set;


public class Card {
    private String name;
    private int copies;
    private int power;
    private Set<Keyword> keywords;
    private Set<Effect> effect;
>>>>>>> 71294da (#43 implement generic websocket message)

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

<<<<<<< HEAD
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

    public List<EffectType> getEffect() {
        return effect;
    }
    public void setEffect(List<EffectType> effect) {
        this.effect = effect;
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
                ", effect=" + effect +
                '}';
    }
}
//
//@Embeddable
//class Effect_Type {
//    private String type;
//    private String target;
//
//    @JsonProperty("effectValue")
//    private int effectValue;
//
//    private String action;
//    @Convert(converter = ConditionConverter.class)
//    private Map<String, Object> condition;
//    private int amount;
//    private String operation;
//    @Convert(converter = ListToJsonConverter.class)
//    private List<String> keywords;
//
//    // Getters and setters
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getTarget() {
//        return target;
//    }
//
//    public void setTarget(String target) {
//        this.target = target;
//    }
//
//    public int getEffectValue() {
//        return effectValue;
//    }
//
//    public void setEffectValue(int effectValue) {
//        this.effectValue = effectValue;
//    }
//
//    public String getAction() {
//        return action;
//    }
//
//    public void setAction(String action) {
//        this.action = action;
//    }
//
//    public Map<String, Object> getCondition() {
//        return condition;
//    }
//
//    public void setCondition(Map<String, Object> condition) {
//        this.condition = condition;
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    public String getOperation() {
//        return operation;
//    }
//
//    public void setOperation(String operation) {
//        this.operation = operation;
//    }
//
//    public List<String> getKeywords() {
//        return keywords;
//    }
//
//    public void setKeywords(List<String> keywords) {
//        this.keywords = keywords;
//    }
//
//    @Override
//    public String toString() {
//        return "EffectType{" +
//                "type='" + type + '\'' +
//                ", target='" + target + '\'' +
//                ", effectValue=" + effectValue +
//                ", action='" + action + '\'' +
//                ", condition=" + condition +
//                ", amount=" + amount +
//                ", operation='" + operation + '\'' +
//                ", keywords=" + keywords +
//                '}';
//    }

=======
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
>>>>>>> 71294da (#43 implement generic websocket message)

