package org.metacorp.mindbug.dto.card;

import lombok.Data;

import java.util.List;

@Data
public class CardSetDTO {
    private String name;
    private List<Integer> cards;
}
