package org.metacorp.mindbug.model.player;

import lombok.Data;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.EffectTiming;

import java.util.*;

/**
 * Class that describes a player data
 */
@Data
public class Player {

    private static final int MAX_HAND_SIZE = 5;

    private UUID uuid;

    private String name;
    private Team team;

    private List<CardInstance> hand;
    private List<CardInstance> board;
    private List<CardInstance> drawPile;
    private List<CardInstance> discardPile;
    private int mindBugs;

    private Set<EffectTiming> disabledTiming;

    public Player(PlayerLightDTO playerData) {
        this.uuid = playerData.getUuid();
        this.name = playerData.getName();

        this.team = new Team();
        hand = new ArrayList<>();
        board = new ArrayList<>();
        discardPile = new ArrayList<>();
        drawPile = new ArrayList<>();
        disabledTiming = new HashSet<>();
        mindBugs = 2;
    }

    public boolean hasMindbug() {
        return mindBugs > 0;
    }

    public void useMindbug() {
        mindBugs--;
    }

    public boolean canTrigger(EffectTiming timing) {
        return !this.disabledTiming.contains(timing);
    }

    public void disableTiming(EffectTiming timing) {
        this.disabledTiming.add(timing);
    }

    public void drawX(int cardsToDraw) {
        while (cardsToDraw != 0 && !drawPile.isEmpty()) {
            hand.add(drawPile.removeFirst());
            cardsToDraw--;
        }
    }

    public void addCardToBoard(CardInstance card) {
        hand.remove(card);
        board.add(card);
    }

    public void addCardToDiscardPile(CardInstance card) {
        board.remove(card);
        discardPile.add(card);
    }

    public Player getOpponent(List<Player> players) {
        Player opponent = null;
        for (Player player : players) {
            if (player.getTeam() != team) {
                opponent = player;
            }
        }

        return opponent;
    }

    public List<CardInstance> getHighestCards() {
        List<CardInstance> highestCards = new ArrayList<>();
        int highestPower = 1;

        for (CardInstance card : board) {
            if (card.getPower() > highestPower) {
                highestPower = card.getPower();
                highestCards.clear();
                highestCards.add(card);
            } else if (card.getPower() == highestPower) {
                highestCards.add(card);
            }
        }

        return highestCards;
    }

    public void refresh(boolean newTurn) {
        refillHand();
        for (CardInstance card : board) {
            card.reset(newTurn);
        }
    }

    /**
     * Draw cards if possible and necessary (hand size < MAX_HAND_SIZE and some cards are still in the draw pile)
     */
    public void refillHand() {
        while (hand.size() < 5 && !drawPile.isEmpty()) {
            hand.add(drawPile.removeFirst());
        }
    }

    /**
     * Checks whether player can block with a creature
     * @return true if he can block with a creature, false otherwise
     */
    public boolean canBlock(boolean sneakyAttack) {
        for (CardInstance card : board) {
            if (card.isAbleToBlock() && (!sneakyAttack || card.hasKeyword(CardKeyword.SNEAKY))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Player (name : " + name + ", team : " + team + ", mindbugs : " + mindBugs + ")";
    }
}
