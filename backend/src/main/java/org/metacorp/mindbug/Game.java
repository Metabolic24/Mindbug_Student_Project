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

    private CardInstance playedCard;
    private CardInstance attackingCard;

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
        if (playedCard != null || currentChoice != null || attackingCard != null) {
            //TODO Throw an error as it is an unexpected situation
            return;
        }

        currentPlayer.getHand().remove(card);
        currentPlayer.refillHand();

        this.playedCard = card;
    }

    // Method executed when a player plays a card, no matter how or why
    public void playCard(boolean mindBug) {
        if (playedCard == null || currentChoice != null || attackingCard != null) {
            //TODO Throw an error as it is an unexpected situation
            return;
        }

        // Update the owner if card has been mindbugged
        if (mindBug) {
            Player newCardOwner = currentPlayer.getOpponent(players);
            if (!newCardOwner.hasMindbug()) {
                // TODO Throw an error as it is an unexpected situation
            }
        }

        managePlayedCard(playedCard);
        resolveEffectQueue(false);

        playedCard = null;
    }

    private void managePlayedCard(CardInstance card) {
        managePlayedCard(card, null);
    }

    protected void managePlayedCard(CardInstance card, Player mindbugger) {
        // Specific behavior if opponent has chosen to mindbug the card
        if (mindbugger != null) {
            card.setOwner(mindbugger);
            mindbugger.useMindbug();
        }

        card.getOwner().getBoard().add(card);

        // Add PLAY effects if player is allowed to trigger them
        addEffectsToQueue(card, EffectTiming.PLAY);

        afterEffect = () -> setCurrentPlayer(card.getOwner().getOpponent(players));
    }

    /**
     * Process attack declaration by triggering the "Attack" effect(s) of the card
     * @param attackCard the attacking card
     */
    public void declareAttack(CardInstance attackCard) {
        if (attackCard == null || !attackCard.isCanAttack() || playedCard != null || currentChoice != null || attackingCard != null) {
            //TODO Throw an error
            return;
        }

        processAttackDeclaration(attackCard);

        resolveEffectQueue(false);
    }

    //TODO A voir si on peut utiliser des mocks côté test plutôt que de séparer ce code de declareAttack
    protected void processAttackDeclaration(CardInstance attackCard) {
        attackingCard = attackCard;

        // Add ATTACK effects if player is allowed to trigger them
        addEffectsToQueue(attackCard, EffectTiming.ATTACK);
    }

    /**
     * Method executed when a player attacks
     * We consider that if attacking creature has HUNTER, the hunting choice has already been resolved through the GUI
     * We consider that if attacking creature has not HUNTER, the opponent has already chosen if he wants to block (and with which creature) or not
     * If creature has FRENZY, then it will be allowed to attack again if it was its first attack.
     */
    public void resolveAttack(CardInstance defendingCard) {
        if (attackingCard == null || (defendingCard != null &&
                (!defendingCard.isCanBlock() || (attackingCard.hasKeyword(Keyword.SNEAKY) && !defendingCard.hasKeyword(Keyword.SNEAKY))))
                        || currentChoice != null || playedCard != null ) {
            // TODO Throw an error as we should not be able to play a card while choice is active (same if some inputs are null)
            return;
        }

        processAttackResolution(attackingCard, defendingCard);

        resolveEffectQueue(false);
    }

    protected void processAttackResolution(CardInstance attackCard, CardInstance defendCard) {
        Player defender = this.getCurrentPlayer().getOpponent(players);

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

        afterEffect = () -> {
            if (attackingCard.isCanAttackTwice()) {
                currentChoice = new FrenzyAttackChoice(attackingCard);
            } else {
                attackingCard.setCanAttackTwice(attackingCard.hasKeyword(Keyword.FRENZY));
                setCurrentPlayer(currentPlayer.getOpponent(getPlayers()));
            }

            attackingCard = null;
        };
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
        if (afterEffect != null) {
            afterEffect.run();
        }
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
