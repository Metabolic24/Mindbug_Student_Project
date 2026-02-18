package org.metacorp.mindbug.service.game.ai;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.ai.AiPlayerTurnAction;
import org.metacorp.mindbug.model.ai.TurnActionType;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BasicAiResolver extends RandomAiResolver {

    @Override
    public boolean shouldMindbug(Game game, AiPlayer player) {
        CardInstance pickedCard = game.getPlayedCard();

        return hasPowerAdvantage(pickedCard, player) ||
                hasSneakyAdvantage(pickedCard, player) ||
                hasHunterAdvantage(pickedCard, player);
    }

    /**
     * @param card   the card to analyze
     * @param player the opponent of the card's owner
     * @return true if the given card has power advantage, false otherwise
     */
    private boolean hasPowerAdvantage(CardInstance card, AiPlayer player) {
        return card.getPower() > CardUtils.getHighestPower(player.getBoard())
                && card.getPower() > CardUtils.getHighestPower(player.getHand());
    }

    /**
     * @param card   the card to analyze
     * @param player the opponent of the card's owner
     * @return true if the given card has SNEAKY advantage, false otherwise
     */
    private boolean hasSneakyAdvantage(CardInstance card, AiPlayer player) {
        return card.hasKeyword(CardKeyword.SNEAKY)
                && !CardUtils.hasKeyword(player.getBoard(), CardKeyword.SNEAKY)
                && !CardUtils.hasKeyword(player.getHand(), CardKeyword.SNEAKY);
    }

    /**
     * @param card   the card to analyze
     * @param player the opponent of the card's owner
     * @return true if the given card has HUNTER advantage, false otherwise
     */
    private boolean hasHunterAdvantage(CardInstance card, AiPlayer player) {
        return card.hasKeyword(CardKeyword.HUNTER)
                && CardUtils.anyPowerLower(player.getBoard(), card.getPower())
                && CardUtils.anyKeywordWithHigherOrEqualPower(player.getBoard(), CardKeyword.HUNTER, card.getPower());
    }

    @Override
    public CardInstance chooseBlocker(List<CardInstance> availableBlockers, Game game) {
        CardInstance attackingCard = game.getAttackingCard();
        List<CardInstance> higherCards = CardUtils.getCardsWithHigherOrEqualPower(availableBlockers, attackingCard.getPower());

        // Never lose life points from an attack if possible
        return higherCards.isEmpty() ?
                availableBlockers.get(RND.nextInt(availableBlockers.size())) :
                higherCards.get(RND.nextInt(higherCards.size()));
    }

    @Override
    public EffectsToApply chooseSimultaneousEffect(Game game) {
        // We choose to not have a complex analysis here as it can be a hard choice
        List<EffectsToApply> shuffledEffects = new ArrayList<>(((SimultaneousEffectsChoice) game.getChoice()).getEffectsToSort());
        Collections.shuffle(shuffledEffects);

        return shuffledEffects.getFirst();
    }

    @Override
    public List<CardInstance> chooseTargets(Game game) {
        return super.chooseTargets(game);
    }

    @Override
    public CardInstance chooseHunterTarget(Game game) {
        CardInstance attackingCard = game.getAttackingCard();
        Player opponent = attackingCard.getOwner().getOpponent(game.getPlayers());
        List<CardInstance> lowerCards = CardUtils.getCardsWithLowerOrEqualPower(opponent.getBoard(), attackingCard.getPower());

        // Do not use hunter if no lower cards
        return lowerCards.isEmpty() ? null : lowerCards.get(RND.nextInt(lowerCards.size()));
    }

    @Override
    public boolean shouldAttackAgain(Game game) {
        CardInstance attackingCard = game.getAttackingCard();
        Player opponent = attackingCard.getOwner().getOpponent(game.getPlayers());

        return CardUtils.noPowerHigher(opponent.getBoard(), attackingCard.getPower());
    }

    @Override
    public boolean resolveBooleanChoice(Game game) {
        // Always return true to boolean choice as it is often giving a bonus
        return true;
    }

    @Override
    public AiPlayerTurnAction getTurnAction(List<CardInstance> availableAttackers, List<CardInstance> availableActionCards, Game game) {
        CardInstance bestAttacker = getBestAttacker(availableAttackers, game);
        if (bestAttacker != null) {
            return new AiPlayerTurnAction(TurnActionType.ATTACK, bestAttacker);
        }

        CardInstance bestAction = getBestAction(availableActionCards);
        if (bestAction != null) {
            return new AiPlayerTurnAction(TurnActionType.ACTION, bestAction);
        }

        CardInstance bestPlayableCard = getBestPlayableCard(game);
        if (bestPlayableCard != null) {
            return new AiPlayerTurnAction(TurnActionType.PLAY, bestPlayableCard);
        } else {
            return new AiPlayerTurnAction(TurnActionType.ATTACK, availableAttackers.get(RND.nextInt(availableAttackers.size())));
        }
    }

    /**
     * @param availableAttackers the cards that can attack
     * @param game               the current game state
     * @return the card that should attack, null if none match
     */
    private CardInstance getBestAttacker(List<CardInstance> availableAttackers, Game game) {
        CardInstance result = null;
        if (!availableAttackers.isEmpty()) {
            // Attack with the highest card if possible
            CardInstance highestCard = CardUtils.getHighestCards(availableAttackers).getFirst();
            Player opponent = highestCard.getOwner().getOpponent(game.getPlayers());
            if (CardUtils.noPowerHigher(opponent.getBoard(), highestCard.getPower())) {
                return highestCard;
            }

            // Attack with SNEAKY card only if there is no higher or equal SNEAKY
            List<CardInstance> sneakyCards = CardUtils.getKeywordCards(availableAttackers, CardKeyword.SNEAKY);
            if (!sneakyCards.isEmpty()) {
                sneakyCards.sort(Comparator.comparingInt(CardInstance::getPower));
                if (!CardUtils.anyKeywordWithHigherOrEqualPower(opponent.getBoard(), CardKeyword.SNEAKY, sneakyCards.getFirst().getPower())) {
                    return sneakyCards.getFirst();
                }
            }

            // Attack with a POISONOUS card (if any)
            List<CardInstance> poisonousCards = CardUtils.getKeywordCards(availableAttackers, CardKeyword.POISONOUS);
            if (!poisonousCards.isEmpty()) {
                result = poisonousCards.get(RND.nextInt(poisonousCards.size()));
            }
        }

        return result;
    }

    /**
     *
     * @param availableActionCards the cards to analyze
     * @return a random Action card if any, null otherwise
     */
    private CardInstance getBestAction(List<CardInstance> availableActionCards) {
        return availableActionCards.isEmpty() ? null : availableActionCards.get(RND.nextInt(availableActionCards.size()));
    }

    /**
     * @param game the current game state
     * @return the best card to be played if any, null otherwise
     */
    private CardInstance getBestPlayableCard(Game game) {
        List<CardInstance> availableCards = game.getCurrentPlayer().getHand();
        if (availableCards.isEmpty()) {
            return null;
        }

        Player opponent = game.getOpponent();

        List<CardInstance> sneakyCards = CardUtils.getKeywordCards(availableCards, CardKeyword.SNEAKY);
        if (!sneakyCards.isEmpty()) {
            List<CardInstance> sneakyHighestPowerCards = CardUtils.getHighestCards(sneakyCards);
            if (CardUtils.noKeywordWithHigherPower(opponent.getBoard(), CardKeyword.SNEAKY, sneakyHighestPowerCards.getFirst().getPower())) {
                return sneakyHighestPowerCards.get(RND.nextInt(sneakyHighestPowerCards.size()));
            }
        }

        List<CardInstance> hunterCards = CardUtils.getKeywordCards(availableCards, CardKeyword.HUNTER);
        if (!hunterCards.isEmpty()) {
            List<CardInstance> hunterHighestPowerCards = CardUtils.getHighestCards(hunterCards);
            if (CardUtils.anyPowerLower(opponent.getBoard(), hunterHighestPowerCards.getFirst().getPower())) {
                return hunterHighestPowerCards.get(RND.nextInt(hunterHighestPowerCards.size()));
            }
        }

        int opponentHighestPower = CardUtils.getHighestPower(opponent.getBoard());
        List<CardInstance> higherCards = CardUtils.getCardsWithHigherOrEqualPower(availableCards, opponentHighestPower);

        if (!higherCards.isEmpty()) {
            return higherCards.get(RND.nextInt(higherCards.size()));
        }

        List<CardInstance> poisonousCards = CardUtils.getKeywordCards(availableCards, CardKeyword.POISONOUS);
        if (!poisonousCards.isEmpty()) {
            return poisonousCards.get(RND.nextInt(poisonousCards.size()));
        }

        return availableCards.get(RND.nextInt(availableCards.size()));
    }
}
