package com.mindbug.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Effect implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String target;
    private int value;
    private String action;
    private int amount;
    private String operation;

    @ElementCollection
    private Set<Keyword> keywords;
    private String triggerCondition;

    @ElementCollection
    @CollectionTable(name = "effect_conditions", joinColumns = @JoinColumn(name = "effect_id"))
    @MapKeyColumn(name = "condition_key")
    @Column(name = "condition_value")
    private Map<String, String> condition;

    @ManyToOne
    @JoinColumn(name = "card_name")
    private Card card;

}
