package com.mindbug.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public int getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(int effectValue) {
        this.effectValue = effectValue;
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