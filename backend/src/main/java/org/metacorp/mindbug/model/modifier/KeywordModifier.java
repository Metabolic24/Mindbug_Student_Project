package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;

/**
 * Modifier that adds a keyword
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeywordModifier extends AbstractModifier<CardKeyword> {

    public KeywordModifier(CardKeyword keyword) {
        this.setValue(keyword);
    }

    @Override
    public void apply(CardInstance cardInstance) {
        CardKeyword keyword = this.getValue();
        if (!cardInstance.hasKeyword(keyword)) {
            cardInstance.getKeywords().add(keyword);
        }
    }
}
