package org.metacorp.mindbug.mapper;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.history.HistoryCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mapper for history data
 */
public class HistoryMapper {

    /**
     * Convert a CardInstance to HistoryCard
     *
     * @param card the card instance
     * @return the corresponding HistoryCard if possible, null otherwise
     */
    public static HistoryCard toHistoryCard(CardInstance card) {
        return card == null ? null : new HistoryCard(card.getUuid(), card.getCard().getName(), card.getOwner().getUuid());
    }

    /**
     * Convert a CardInstance to HistoryCard
     *
     * @param cards the card instance list
     * @return the corresponding HistoryCard if possible, null otherwise
     */
    public static List<HistoryCard> toHistoryCards(Collection<CardInstance> cards) {
        return cards == null ? new ArrayList<>() : cards.stream().map(HistoryMapper::toHistoryCard).toList();
    }
}
