package com.mindbug.model;

import java.util.List;
import java.util.Map;

public class Card {
    private String name;
    private int copies;
    private int power;
    private List<String> keywords;
    private String trigger_condition;
    private List<EffectType> effect_type;

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
}

class EffectType {
    private String type;
    private String target;
    private int value;
    private String action;
    private Map<String, Object> condition;
    private int amount;
    private String operation; 
    private boolean exclude_self; 
    private List<String> keywords;
    private boolean your_turn;
    private boolean mirror;

    public boolean isMirror() {
        return this.mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public boolean isYour_turn() {
        return this.your_turn;
    }

    public void setYour_turn(boolean your_turn) {
        this.your_turn = your_turn;
    }

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

    public boolean isExclude_self() {
        return exclude_self;
    }

    public void setExclude_self(boolean exclude_self) {
        this.exclude_self = exclude_self;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keyword) {
        this.keywords = keyword;
    }
}
