package org.metacorp.mindbug.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.metacorp.mindbug.model.card.CardKeyword;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for card data
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonPropertyOrder({"uuid", "ownerId", "id", "setName", "name", "power", "keywords","hasAction" , "stillTough", "ableToBlock", "ableToAttack", "ableToAttackTwice"})
public class CardDTO {
    private UUID uuid;
    private UUID ownerId;
    private int id;
    private String setName;
    private String name;
    private int power;
    private Set<CardKeyword> keywords;
    private boolean hasAction;
    private boolean stillTough;
    private boolean ableToBlock;
    private boolean ableToAttack;
    private boolean ableToAttackTwice;
}
