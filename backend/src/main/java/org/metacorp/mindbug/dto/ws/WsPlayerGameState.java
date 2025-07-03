package org.metacorp.mindbug.dto.ws;

import lombok.Data;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.PlayerDTO;
import org.metacorp.mindbug.dto.choice.AbstractChoiceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for game state data related to a specific player
 */
@Data
public class WsPlayerGameState {

    private UUID uuid;
    private PlayerDTO player;
    private PlayerDTO opponent;
    private Boolean playerTurn;
    private UUID winner;

    private CardDTO card;
    private AbstractChoiceDTO choice;

    /**
     * Constructor
     *
     * @param gameState the GameStateDTO to
     * @param isPlayer  is it a DTO for the current player or his/her opponent
     */
    public WsPlayerGameState(GameStateDTO gameState, boolean isPlayer) {
        this.uuid = gameState.getUuid();
        this.player = isPlayer ? gameState.getPlayer() : gameState.getOpponent();
        this.opponent = isPlayer ? gameState.getOpponent() : gameState.getPlayer();
        this.playerTurn = isPlayer;
        this.winner = gameState.getWinner();
        this.card = gameState.getCard();
        this.choice = gameState.getChoice();

        List<CardDTO> opponentHand = new ArrayList<>(this.opponent.getHand().size());
        for (CardDTO cardDTO : this.opponent.getHand()) {
            CardDTO copy = new CardDTO();
            copy.setUuid(cardDTO.getUuid());
            opponentHand.add(copy);
        }

        this.opponent = new PlayerDTO(this.opponent);
        this.opponent.setHand(opponentHand);
    }
}
