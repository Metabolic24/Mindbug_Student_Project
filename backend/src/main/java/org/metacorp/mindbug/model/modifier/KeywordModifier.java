package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.model.card.CardKeyword;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeywordModifier extends AbstractModifier<CardKeyword>{

    public KeywordModifier(CardKeyword keyword) {
        super();
        this.setType(AttackModifierType.KEYWORD);
        this.setValue(keyword);
    }
}
