package org.metacorp.mindbug.utils;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for logging
 */
public class LogUtils {

    public static String getLoggablePlayer(Player player) {
        return MessageFormat.format("{0} ({1})", player.getName(), player.getUuid());
    }

    public static String getLoggablePlayers(List<Player> players) {
        return players.stream().map(LogUtils::getLoggablePlayer).collect(Collectors.joining(", "));
    }

    public static String getLoggableCard(CardInstance card) {
        return MessageFormat.format("{0} ({1})", card.getCard().getName(), card.getUuid());
    }

    public static String getLoggableCards(Collection<CardInstance> cards) {
        return cards.stream().map(LogUtils::getLoggableCard).collect(Collectors.joining(" ;\n- ", "{\n- ", "\n}"));
    }

    /**
     * Constructor
     */
    private LogUtils() {
        // Not to be used
    }
}
