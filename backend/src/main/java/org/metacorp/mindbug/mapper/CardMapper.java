package org.metacorp.mindbug.mapper;

import org.metacorp.mindbug.dto.card.LightCardDTO;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.impl.EvolveEffect;

import java.util.List;

public class CardMapper {

    /**
     * Transform a CardInstance into a lightweight card DTO (only id and setName filled)
     *
     * @param card the CardInstance to transform
     * @return the corresponding LightCardDTO
     */
    public static LightCardDTO fromCard(Card card) {
        LightCardDTO result = new LightCardDTO();
        result.setId(card.getId());
        result.setPower(card.getPower());
        result.setKeywords(card.getKeywords());
        result.setSetName(card.getSetName());
        result.setParentId(card.getInitialCardId());
        result.setUnique(card.isUnique());

        List<Effect> actionEffects = card.getEffects().get(EffectTiming.ACTION);
        if (actionEffects != null) {
            for (Effect effect : actionEffects) {
                if (effect.getType() == EffectType.EVOLVE) {
                    EvolveEffect evolveEffect = (EvolveEffect) effect;
                    result.setEvolutionId(evolveEffect.getId());
                }
            }
        }

        return result;
    }
}
