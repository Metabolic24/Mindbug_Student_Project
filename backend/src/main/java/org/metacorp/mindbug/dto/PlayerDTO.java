package org.metacorp.mindbug.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.metacorp.mindbug.model.effect.EffectTiming;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for current player data
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonPropertyOrder({"uuid", "name", "lifePoints", "mindbugCount", "disabledTiming", "drawPileCount", "hand", "board", "discard"})
public class PlayerDTO {
    private UUID uuid;
    private String name;
    private Integer lifePoints;
    private Integer mindbugCount;
    private Set<EffectTiming> disabledTiming;

    private Integer drawPileCount;
    private List<CardDTO> board;
    private List<CardDTO> discard;
    private List<CardDTO> hand;

    /**
     * Clone constructor
     * @param clone the cloned PlayerDTO
     */
    public PlayerDTO(PlayerDTO clone) {
        this.uuid = clone.getUuid();
        this.name = clone.getName();
        this.lifePoints = clone.getLifePoints();
        this.mindbugCount = clone.getMindbugCount();
        this.disabledTiming = clone.getDisabledTiming();
        this.drawPileCount = clone.getDrawPileCount();
        this.hand = clone.getHand();
        this.board = clone.getBoard();
        this.discard = clone.getDiscard();
    }
}
