package org.metacorp.mindbug.service;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.FrenzyAttackChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.player.Player;

import java.util.Map;

/**
 * Utility service that updates game state when a player attacks
 */
public class AttackService {

    // Not to be used
    private AttackService() {
        // Nothing to do
    }

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
            throw new GameStateException("attacking card should not be able to attack", Map.of("attackCard", attackCard));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before attacking", Map.of("attackingCard", game.getAttackingCard()));
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before attacking", Map.of("choice", game.getChoice()));
        } else if (game.getPlayedCard() != null) {
            throw new GameStateException("a played card needs to be resolved before attacking", Map.of("playedCard", game.getPlayedCard()));
        }

        processAttackDeclaration(attackCard, game);

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

        // Add ATTACK effects if the player is allowed to trigger them
        EffectQueueService.addBoardEffectsToQueue(attackCard, EffectTiming.ATTACK, game.getEffectQueue());

        game.setAfterEffect(() -> {
            Player cardOwner = attackCard.getOwner();
            if (game.getCurrentPlayer().equals(cardOwner) && cardOwner.getBoard().contains(attackCard) && !game.getOpponent().canBlock()) {
                try {
                    resolveAttack(null, game);
                } catch (GameStateException e) {
                    // TODO Manage errors
                }
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
            if(defendingCard.getOwner().equals(attackingCard.getOwner())) {
                throw new GameStateException("player cannot defend its own attack", Map.of("defendingCard", defendingCard));
            } else if (!defendingCard.isAbleToBlock()) {
                throw new GameStateException("defending card is not able to block", Map.of("defendingCard", defendingCard));
            } else if (attackingCard.hasKeyword(CardKeyword.SNEAKY) && !defendingCard.hasKeyword(CardKeyword.SNEAKY)) {
                throw new GameStateException("defending card cannot defend a SNEAKY attack", Map.of("attackingCard", game.getAttackingCard(), "defendingCard", defendingCard));
            }
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before attacking", Map.of("choice", game.getChoice()));
        } else if (game.getPlayedCard() != null) {
            throw new GameStateException("a played card needs to be resolved before attacking", Map.of("playedCard", game.getPlayedCard()));
        }

        // Specific case that occurs when the ATTACK effect(s) of the card caused its destruction
        if (!attackingCard.getOwner().getBoard().contains(attackingCard)) {
            System.out.println("*********************************************************");
            System.out.println("Attacking card has been destroyed and is no more on board");
            System.out.println("*********************************************************");

            game.setAttackingCard(null);
            return;
        }

        processAttackResolution(attackingCard, defendingCard, game);

        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Process the attack resolution
     *
     * @param attackCard the card that attacked
     * @param defendCard the card chosen to defend the attack (may be null if player chooses to not block)
     * @param game       the game state
     */
    protected static void processAttackResolution(CardInstance attackCard, CardInstance defendCard, Game game) {
        if (defendCard == null) {
            Player defender = game.getOpponent();
            defender.getTeam().loseLifePoints(1);
            GameService.lifePointLost(defender, game);
        } else {
            if (attackCard.getPower() > defendCard.getPower()) {
                GameService.defeatCard(defendCard, game.getEffectQueue());

                if (defendCard.hasKeyword(CardKeyword.POISONOUS)) {
                    GameService.defeatCard(attackCard, game.getEffectQueue());
                }
            } else {
                GameService.defeatCard(attackCard, game.getEffectQueue());

                if (attackCard.hasKeyword(CardKeyword.POISONOUS) || attackCard.getPower() == defendCard.getPower()) {
                    GameService.defeatCard(defendCard, game.getEffectQueue());
                }
            }
        }

        GameService.refreshGameState(game);

        game.setAfterEffect(() -> {
            CardInstance attackingCard = game.getAttackingCard();
            if (attackingCard.getOwner().getBoard().contains(attackingCard) && attackingCard.isAbleToAttackTwice() && attackingCard.isAbleToAttack()) {
                game.setChoice(new FrenzyAttackChoice(attackingCard));
            } else {
                attackingCard.setAbleToAttackTwice(attackingCard.hasKeyword(CardKeyword.FRENZY));
                game.setCurrentPlayer(game.getOpponent());
            }

            game.setAttackingCard(null);
        });
    }
}
