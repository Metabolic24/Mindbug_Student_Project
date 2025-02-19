package com.mindbug.model;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
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

    public void setKeywords(List<String> keyword) {
        this.keywords = keyword;
    }
}
