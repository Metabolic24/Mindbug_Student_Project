package com.mindbug.models;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mindbug.models.Converter.ConditionConverter;
import com.mindbug.models.Converter.ListToJsonConverter;
import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

@Embeddable
public class EffectType {
    private String type;
    private String target;
    @JsonProperty("effectValue")
    private int effectValue;
    private String action;
    @Convert(converter = ConditionConverter.class)
    private Map<String, Object> condition;
    private int amount;
    private String operation;
    @Convert(converter = ListToJsonConverter.class)
    private List<String> keywords;
    private String trigger_condition;
    //private int effectValueField;// Added field to match JSON

    // Getters, setters, and toString method

=======
import java.util.List;
import java.util.Map;

public class EffectType {
    private String type;
    private String target;
    private int value;
    private String action;
    private Map<String, Object> condition;
    private int amount;
    private String operation; 
    private List<String> keywords;

    // Getters and Setters
>>>>>>> 71294da (#43 implement generic websocket message)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

<<<<<<< HEAD
    public int getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(int effectValue) {
        this.effectValue = effectValue;
=======
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
>>>>>>> 71294da (#43 implement generic websocket message)
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<String> getKeywords() {
        return keywords;
    }

<<<<<<< HEAD
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getTrigger_condition() {
        return trigger_condition;
    }

    public void setTrigger_condition(String trigger_condition) {
        this.trigger_condition = trigger_condition;
    }


    @Override
    public String toString() {
        return "EffectType{" +
                "type='" + type + '\'' +
                ", target='" + target + '\'' +
                ", effectValue=" + effectValue +
                ", action='" + action + '\'' +
                ", condition=" + condition +
                ", amount=" + amount +
                ", operation='" + operation + '\'' +
                ", keywords=" + keywords +
                ", trigger_condition='" + trigger_condition + '\'' +

                '}';
    }
}
=======
    public void setKeywords(List<String> keyword) {
        this.keywords = keyword;
    }
}
>>>>>>> 71294da (#43 implement generic websocket message)
