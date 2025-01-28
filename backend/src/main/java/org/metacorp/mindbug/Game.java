package org.metacorp.mindbug;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.card.effect.revive.ReviveEffect;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.choice.frenzy.FrenzyAttackChoice;
import org.metacorp.mindbug.choice.simultaneous.SimultaneousEffectsChoice;
import org.metacorp.mindbug.player.Player;

import java.util.*;

/**
 * Main class that manages game workflow
 */
@Getter
@Setter
public class Game {

    private List<Player> players;
    private Player currentPlayer;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;

    private final Queue<EffectToApply> effectQueue;

    private IChoice<?> currentChoice;

    private Runnable afterEffect;

    /**
     * Empty constructor (WARNING : a game is not meant to be reused)
     */
    public Game(String player1, String player2) {
        bannedCards = new ArrayList<>();
        players = new ArrayList<>(2);
        effectQueue = new LinkedList<>();

        players.add(new Player(player1));
        players.add(new Player(player2));

        start();
    }

    // Start a new game
    private void start() {
        cards = Utils.getCardsFromConfig("default.json");

        Collections.shuffle(cards);

        for (Player player : players) {
            initDrawAndHand(player);
            player.getTeam().setLifePoints(3);
        }

        this.currentPlayer = getFirstPlayer();
    }

    public void nextTurn() {
        this.currentPlayer = this.currentPlayer.getOpponent(this.players);
    }

    // Method executed when a player choose a card that he would like to play
    public void pickCard(CardInstance card) {
        card.getOwner().refillHand();
    }

    // Method executed when a player plays a card, no matter how or why
    public void playCard(CardInstance card, boolean mindBug) {
        if (card == null || currentChoice != null) {
            //TODO Throw an error as it is an unexpected situation
            return;
        }

        // Update the owner if card has been mindbugged
        if (mindBug) {
            Player newCardOwner = card.getOwner().getOpponent(players);
            if (!newCardOwner.hasMindbug()) {
                // TODO Throw an error as it is an unexpected situation
            }

            card.setOwner(newCardOwner);
            newCardOwner.useMindbug();
        }

        managePlayedCard(card, mindBug);
        resolveEffectQueue(false);
    }

    protected void managePlayedCard(CardInstance card, boolean mindBug) {
        Player cardOwner = card.getOwner();
        cardOwner.addCardToBoard(card);

        // Add PLAY effects if player is allowed to trigger them
        addEffectsToQueue(card, EffectTiming.PLAY);

        afterEffect = () -> setCurrentPlayer(cardOwner.getOpponent(players));
    }

    /**
     * Method executed when a player attacks
     * We consider that if attacking creature has HUNTER, the hunting choice has already been resolved through the GUI
     * We consider that if attacking creature has not HUNTER, the opponent has already chosen if he wants to block (and with which creature) or not
     * We consider that if attacking creature has SNEAKY, the GUI correctly restricted the creatures allowed to block.
     * If creature has FRENZY, then it will be allowed to attack again if it was its first attack.
     */
    public void attack(AttackHolder attackHolder) {
        if (attackHolder.getAttackCard() == null || attackHolder.getDefender() == null ||
                !attackHolder.getAttackCard().isCanAttack() || (attackHolder.getDefendCard() != null && !attackHolder.getDefendCard().isCanBlock())
                || (attackHolder.getAttackCard().hasKeyword(Keyword.SNEAKY) && attackHolder.getDefendCard() != null && !attackHolder.getDefendCard().hasKeyword(Keyword.SNEAKY))
                || currentChoice != null) {
            // TODO Throw an error as we should not be able to play a card while choice is active (same if some inputs are null)
            return;
        }

        manageAttack(attackHolder.getAttackCard(), attackHolder.getDefendCard(), attackHolder.getDefender());
        resolveEffectQueue(false);
    }

    // This method has been separated from attack one to ease unit tests
    protected void manageAttack(CardInstance attackCard, CardInstance defendCard, Player defender) {
        afterEffect = () -> {
            resolveAttack(attackCard, defendCard, defender);

            if (attackCard.isCanAttackTwice()) {
                currentChoice = new FrenzyAttackChoice(attackCard);
            } else {
                attackCard.setCanAttackTwice(attackCard.hasKeyword(Keyword.FRENZY));
                setCurrentPlayer(currentPlayer.getOpponent(getPlayers()));
            }
        };

        // Add ATTACK effects if player is allowed to trigger them
        addEffectsToQueue(attackCard, EffectTiming.ATTACK);
    }

    protected void resolveAttack(CardInstance attackCard, CardInstance defendCard, Player defender) {
        if (defendCard == null) {
            defender.getTeam().loseLifePoints(1);
            lifePointLost(defender);
        } else {
            if (attackCard.getPower() > defendCard.getPower()) {
                defeatCard(defendCard);

                if (defendCard.hasKeyword(Keyword.POISONOUS)) {
                    defeatCard(attackCard);
                }
            } else {
                defeatCard(attackCard);

                if (attackCard.hasKeyword(Keyword.POISONOUS) || attackCard.getPower() == defendCard.getPower()) {
                    defeatCard(defendCard);
                }
            }
        }
    }

    public void applyChoice(List<UUID> sortedChoiceIDs) {
        // Retrieve the current choice and check it exists
        if (this.currentChoice == null || sortedChoiceIDs == null || this.currentChoice.getType() != ChoiceType.SIMULTANEOUS) {
            //TODO Raise an error
            return;
        }

        SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) currentChoice;
        simultaneousEffectsChoice.resolve(this, sortedChoiceIDs);

        resolveEffectQueue(true);
    }

    public void addEffectsToQueue(CardInstance card, EffectTiming timing) {
        if (card.getOwner().canTrigger(timing)) {
            effectQueue.addAll(card.getEffects(timing).stream()
                    .map(effect -> new EffectToApply(effect, card, this))
                    .toList());
        } else {
            //TODO Voir s'il faut faire quelque chose à ce moment-là
        }
    }

    public void resolveEffectQueue(boolean fromSimultaneousChoice) {
        if (currentChoice != null) {
            // TODO Maybe raise an error at the start if a choice is remaining
        }

        if (!fromSimultaneousChoice && effectQueue.size() >= 2) {
            currentChoice = new SimultaneousEffectsChoice(currentPlayer, new HashSet<>(effectQueue));
            effectQueue.clear();
            return;
        }

        while (!effectQueue.isEmpty()) {
            EffectToApply currentEffect = effectQueue.peek();
            currentEffect.getEffect().apply(this, currentEffect.getCard());

            // TODO Faut-il envisager un système transactionnel ou similaire pour gérer le fait qu'une application d'effet puisse échouer sans pour autant dégrader l'état actuel du jeu?
            // Only remove effect from queue after it is applied to avoid loss of data
            effectQueue.remove(currentEffect);

            // If there is many remaining effects, then create a simultaneous choice
            if (currentChoice == null && effectQueue.size() >= 2) {
                currentChoice = new SimultaneousEffectsChoice(currentPlayer, new HashSet<>(effectQueue));
                effectQueue.clear();
                return;
            }
        }

        // Execute the after effect when queue is empty
        afterEffect.run();
    }

    public void lifePointLost(Player player) {
        if (player.getTeam().getLifePoints() <= 0) {
            endGame(player);
            return;
        }

        for (CardInstance card : player.getDiscardPile()) {
            List<AbstractEffect> effects = card.getEffects(EffectTiming.PASSIVE).stream().filter(effect -> effect instanceof ReviveEffect).toList();
            if (!effects.isEmpty()) {
                // We consider that, for the moment, it is not necessary to create a simultaneous choice here even if there are multiple cards to revive
                effectQueue.add(new EffectToApply(effects.getFirst(), card, this));
            }
        }
    }

    public void resetChoice() {
        this.currentChoice = null;
    }

    private void endGame(Player loser) {
        Player winner = loser.getOpponent(players);

        System.out.printf("%s wins ; %s loses", winner.getName(), loser.getName());
    }

    public void defeatCard(CardInstance card) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            card.getOwner().addCardToDiscardPile(card);
            addEffectsToQueue(card, EffectTiming.DEFEATED);
        }
    }

    // Return the first player of the game (should only be used once per game)
    private Player getFirstPlayer() {
        List<Player> validPlayers = new ArrayList<>(players);
        while (validPlayers.size() != 1) {
            int higherPower = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                // Get a random card from the remaining cards
                CardInstance firstPlayerCard = banCard();

                if (firstPlayerCard.getPower() < higherPower) {
                    // Current player will not be the first one
                    continue;
                } else if (firstPlayerCard.getPower() > higherPower) {
                    // Update higherPower value and clean the next players set
                    higherPower = firstPlayerCard.getPower();
                    nextPlayers.clear();
                } else {
                    // Nothing to do, we will just add the player to the nextPlayers set
                }

                nextPlayers.add(player);
            }

            validPlayers = nextPlayers;
        }

        return validPlayers.getFirst();
    }

    // Randomly choose a card and exclude it from the current game
    private CardInstance banCard() {
        int cardIndex = new Random().nextInt(cards.size() - 1);
        CardInstance chosenCard = cards.remove(cardIndex);
        bannedCards.add(chosenCard);

        return chosenCard;
    }

    // Fill the hand and draw pile of the given player
    private void initDrawAndHand(Player player) {
        List<CardInstance> hand = player.getHand();
        while (hand.size() != 5) {
            int cardIndex = new Random().nextInt(cards.size() - 1);

            CardInstance currentCard = cards.remove(cardIndex);
            currentCard.setOwner(player);
            hand.add(currentCard);
        }

        List<CardInstance> drawPile = player.getDrawPile();
        while (drawPile.size() != 5) {
            int cardIndex = new Random().nextInt(cards.size() - 1);

            CardInstance currentCard = cards.remove(cardIndex);
            currentCard.setOwner(player);
            drawPile.add(currentCard);
        }
    }
}
