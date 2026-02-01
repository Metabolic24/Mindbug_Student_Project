package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

/**
 * Utility service that updates game state when a player attacks
 */
public class AttackService {
    /**
     * Method executed when a player declares an attack
     *
     * @param attackCard the attacking card
     * @param game       the game state
     */
    public static void declareAttack(CardInstance attackCard, Game game) throws GameStateException {
        if (attackCard == null) {
            throw new GameStateException("no attacking card");
        } else if (!attackCard.isAbleToAttack()) {
            throw new GameStateException("attacking card should not be able to attack",
                    Map.of("attackCard", attackCard));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before attacking",
                    Map.of("attackingCard", game.getAttackingCard()));
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before attacking",
                    Map.of("choice", game.getChoice()));
        } else if (game.getPlayedCard() != null) {
            throw new GameStateException("a played card needs to be resolved before attacking",
                    Map.of("playedCard", game.getPlayedCard()));
        }

        processAttackDeclaration(attackCard, game);

        // Send update through WebSocket
        WebSocketService.sendGameEvent(WsGameEventType.ATTACK_DECLARED, game);
        HistoryService.log(game, HistoryKey.ATTACK, attackCard);

        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Process attack declaration by triggering the "Attack" effect(s) of the card
     *
     * @param attackCard the attacking card
     * @param game       the game state
     */
    protected static void processAttackDeclaration(CardInstance attackCard, Game game) {
        game.setAttackingCard(attackCard);
        final Player attackCardOwner = attackCard.getOwner();

        // Add ATTACK effects if the player is allowed to trigger it
        EffectQueueService.addBoardEffectsToQueue(attackCard, EffectTiming.ATTACK, game.getEffectQueue());

        game.setAfterEffect(() -> {
            if (attackCardOwner.getBoard().contains(attackCard)) {
                Player defender = attackCardOwner.getOpponent(game.getPlayers());
                if (defender.getBoard().isEmpty()) {
                    try {
                        resolveAttack(null, game);
                    } catch (GameStateException e) {
                        // TODO Manage errors
                        e.printStackTrace();
                    }
                } else if (game.getForcedTarget() != null) {
                    try {
                        resolveAttack(game.getForcedTarget(), game);
                    } catch (GameStateException e) {
                        // TODO Manage errors
                        e.printStackTrace();
                    }
                } else if (attackCard.hasKeyword(CardKeyword.HUNTER)) {
                    game.setChoice(new HunterChoice(attackCard, new HashSet<>(defender.getBoard())));
                    WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
                } else if (!defender.canBlock(attackCard.hasKeyword(CardKeyword.SNEAKY))) {
                    try {
                        resolveAttack(null, game);
                    } catch (GameStateException e) {
                        // TODO Manage errors
                        e.printStackTrace();
                    }
                } else {
                    WebSocketService.sendGameEvent(WsGameEventType.WAITING_ATTACK_RESOLUTION, game);
                }
            } else {
                game.setAttackingCard(null);

                GameStateService.newTurn(game);
            }
        });
    }

    /**
     * Method executed when a player answer to its opponent attack<br>
     * We consider that if attacking creature has HUNTER, the hunting choice has already been resolved through the GUI<br>
     * We consider that if attacking creature has not HUNTER, the opponent has already chosen if he wants to block (and with which creature) or not<br>
     *
     * @param defendingCard the card chosen to defend the attack
     * @param game          the game state
     */
    public static void resolveAttack(CardInstance defendingCard, Game game) throws GameStateException {
        CardInstance attackingCard = game.getAttackingCard();
        if (attackingCard == null) {
            throw new GameStateException("no attacking card set in game state");
        } else if (defendingCard != null) {
            if (defendingCard.getOwner().equals(attackingCard.getOwner())) {
                throw new GameStateException("player cannot defend its own attack",
                        Map.of("defendingCard", defendingCard));
            } else if (!defendingCard.isAbleToBlock() && !attackingCard.hasKeyword(CardKeyword.HUNTER)) {
                throw new GameStateException("defending card is not able to block",
                        Map.of("defendingCard", defendingCard));
            } else if (attackingCard.hasKeyword(CardKeyword.SNEAKY) && !defendingCard.hasKeyword(CardKeyword.SNEAKY)
                    && !attackingCard.hasKeyword(CardKeyword.HUNTER)) {
                throw new GameStateException("defending card cannot defend a SNEAKY attack",
                        Map.of("attackingCard", game.getAttackingCard(), "defendingCard", defendingCard));
            } else if (game.getForcedTarget() != null && !game.getForcedTarget().equals(defendingCard)) {
                throw new GameStateException("invalid defending card : only one target allowed",
                        Map.of("defendingCard", defendingCard, "forcedTarget", game.getForcedTarget()));
            }
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before attacking", Map.of("choice", game.getChoice()));
        } else if (game.getPlayedCard() != null) {
            throw new GameStateException("a played card needs to be resolved before attacking", Map.of("playedCard", game.getPlayedCard()));
        }

        processAttackResolution(attackingCard, defendingCard, game);

        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Process the attack resolution
     *
     * @param attackCard the card that attacked
     * @param defendCard the card chosen to defend the attack (may be null if the player chooses to not block)
     * @param game       the game state
     */
    protected static void processAttackResolution(CardInstance attackCard, CardInstance defendCard, Game game) {
        // First log the block action in the history
        HistoryService.log(game, HistoryKey.BLOCK, attackCard, defendCard == null ? null : Collections.singleton(defendCard));

        if (defendCard == null) {
            Player defender = attackCard.getOwner().getOpponent(game.getPlayers());
            defender.getTeam().loseLifePoints(1);
            GameStateService.lifePointLost(defender, game);
        } else {
            boolean reversedFight = attackCard.hasKeyword(CardKeyword.REVERSED) || defendCard.hasKeyword(CardKeyword.REVERSED);

            if ((attackCard.getPower() > defendCard.getPower() && !reversedFight)
                    || (attackCard.getPower() < defendCard.getPower() && reversedFight)) {
                CardService.defeatCard(defendCard, game);

                if (defendCard.hasKeyword(CardKeyword.POISONOUS)) {
                    CardService.defeatCard(attackCard, game);
                }
            } else {
                CardService.defeatCard(attackCard, game);

                if (attackCard.hasKeyword(CardKeyword.POISONOUS) || attackCard.getPower() == defendCard.getPower()) {
                    CardService.defeatCard(defendCard, game);
                }
            }
        }

        // Check that the game is not finished as a direct may have ended it
        if (!game.isFinished()) {
            GameStateService.refreshGameState(game);

            game.setAfterEffect(() -> {
                CardInstance attackingCard = game.getAttackingCard();
                if (attackingCard.getOwner().getBoard().contains(attackingCard) && attackingCard.isAbleToAttackTwice()
                        && attackingCard.isAbleToAttack()) {
                    game.setChoice(new FrenzyAttackChoice(attackingCard));

                    WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
                    HistoryService.logChoice(game);
                } else {
                    attackingCard.setAbleToAttackTwice(attackingCard.hasKeyword(CardKeyword.FRENZY));
                    GameStateService.newTurn(game);
                }

                game.setAttackingCard(null);
                game.setForcedTarget(null);
                game.setForcedAttack(false);
            });
        }
    }

    /**
     * Constructor
     */
    private AttackService() {
        // Not to be used
    }
}
