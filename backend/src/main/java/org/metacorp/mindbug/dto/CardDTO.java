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
@JsonPropertyOrder({"uuid", "name", "power", "keywords", "stillTough", "ableToBlock", "ableToAttack", "ableToAttackTwice"})
public class CardDTO {
    private UUID uuid;
    private String name;
    private int power;
    private Set<CardKeyword> keywords;
    private boolean stillTough;
    private boolean ableToBlock;
    private boolean ableToAttack;
    private boolean ableToAttackTwice;
}
