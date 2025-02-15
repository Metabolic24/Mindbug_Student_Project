package org.metacorp.mindbug.game;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.Keyword;
import org.metacorp.mindbug.card.effect.*;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.choice.frenzy.FrenzyAttackChoice;
import org.metacorp.mindbug.choice.simultaneous.SimultaneousEffectsChoice;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.*;

/**
 * Main class that manages game workflow
 */
@Getter
@Setter
public class Game {

    private List<Player> players;
    private Player currentPlayer;
    private boolean finished;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;

    private CardInstance playedCard;
    private CardInstance attackingCard;

    private final Queue<EffectToApply> effectQueue;
    private IChoice<?> choice;
    private Runnable afterEffect;

    /**
     * Empty constructor (WARNING : a game is not meant to be reused)
     */
    public Game(String player1, String player2) {
        finished = false;
        bannedCards = new ArrayList<>();
        players = new ArrayList<>(2);
        effectQueue = new LinkedList<>();

        players.add(new Player(player1));
        players.add(new Player(player2));

        start();
    }

    // Start a new game
    private void start() {
        cards = CardUtils.getCardsFromConfig("default.json");

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
        if (playedCard != null || choice != null || attackingCard != null) {
            System.err.println("Erreur : pickCard");
            //TODO Throw an error as it is an unexpected situation
            return;
        }

        currentPlayer.getHand().remove(card);
        currentPlayer.refillHand();

        this.playedCard = card;
    }

    // Method executed when a player plays a card, no matter how or why
    public void playCard(boolean mindBug) {
        if (playedCard == null || choice != null || attackingCard != null) {
            System.err.println("Erreur : playCard");
            //TODO Throw an error as it is an unexpected situation
            return;
        }

        Player newCardOwner;

        // Update the owner if card has been mindbugged
        if (mindBug) {
            newCardOwner = currentPlayer.getOpponent(players);
            if (!newCardOwner.hasMindbug()) {
                System.err.println("Erreur : playCard Mindbug");
                // TODO Throw an error as it is an unexpected situation
            }
        } else {
            newCardOwner = currentPlayer;
        }

        managePlayedCard(newCardOwner);
        resolveEffectQueue(false);
    }

    protected void managePlayedCard(Player newCardOwner) {
        // Specific behavior if opponent has chosen to mindbug the card
        if (!playedCard.getOwner().equals(newCardOwner)) {
            playedCard.setOwner(newCardOwner);
            newCardOwner.useMindbug();
        }

        newCardOwner.getBoard().add(playedCard);

        refreshBoard();

        // Add PLAY effects if player is allowed to trigger them
        addEffectsToQueue(playedCard, EffectTiming.PLAY);

        afterEffect = () -> {
            playedCard = null;
            nextTurn();
        };
    }

    /**
     * Process attack declaration by triggering the "Attack" effect(s) of the card
     * @param attackCard the attacking card
     */
    public void declareAttack(CardInstance attackCard) {
        if (attackCard == null || !attackCard.isCanAttack() || playedCard != null || choice != null || attackingCard != null) {
            System.err.println("Erreur : declareAttack");
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
                        || choice != null || playedCard != null ) {
            System.err.println("Erreur : resolveAttack");
            // TODO Throw an error as we should not be able to play a card while choice is active (same if some inputs are null)
            return;
        }

        if (!attackingCard.getOwner().getBoard().contains(attackingCard)) {
            System.out.println("*********************************************************");
            System.out.println("Attacking card has been destroyed and is no more on board");
            System.out.println("*********************************************************");
            attackingCard = null;
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

        refreshBoard();

        afterEffect = () -> {
            if (attackingCard.getOwner().getBoard().contains(attackingCard) && attackingCard.isCanAttackTwice() && attackingCard.isCanAttack()) {
                choice = new FrenzyAttackChoice(attackingCard);
            } else {
                attackingCard.setCanAttackTwice(attackingCard.hasKeyword(Keyword.FRENZY));
                nextTurn();
            }

            attackingCard = null;
        };
    }

    public <T> void  resolveChoice(T data) {
        if (this.choice == null || data == null) {
            System.err.println("Erreur : resolveChoice");
            //TODO Raise an error
            return;
        }

        try {
            IChoice<T> choice = (IChoice<T>)this.choice;
            choice.resolve(this, data);

            refreshBoard();
            resolveEffectQueue(true);
        } catch (ClassCastException e) {
            e.printStackTrace();
            //TODO Raise an error
        }
    }

    public void addEffectsToQueue(CardInstance card, EffectTiming timing) {
        List<AbstractEffect> effects = card.getEffects(timing);
        if (!effects.isEmpty()) {
            if (card.getOwner().canTrigger(timing)) {
                effectQueue.add(new EffectToApply(effects, card, this));
            } else {
                System.out.printf("Effets %s annulés\n", timing);
                //TODO Voir s'il faut faire quelque chose à ce moment-là
            }
        }
    }

    public void resolveEffectQueue(boolean fromSimultaneousChoice) {
        if (choice != null) {
            System.err.println("Erreur : resolveEffectQueue choice");
            // TODO Maybe raise an error at the start if a choice is remaining
        }

        if (!fromSimultaneousChoice && effectQueue.size() >= 2) {
            choice = new SimultaneousEffectsChoice(currentPlayer, new HashSet<>(effectQueue));
            effectQueue.clear();
            return;
        }

        while (!effectQueue.isEmpty()) {
            EffectToApply currentEffect = effectQueue.peek();
            currentEffect.getEffects().forEach(effect -> effect.apply(this, currentEffect.getCard())); //TODO Il n'y a pas de refresh entre les effets d'une même carte ni de vérification de fin de partie

            if (finished) {
                return;
            }

            // TODO Faut-il envisager un système transactionnel ou similaire pour gérer le fait qu'une application d'effet puisse échouer sans pour autant dégrader l'état actuel du jeu?
            // Only remove effect from queue after it is applied to avoid loss of data
            effectQueue.remove(currentEffect);

            // If there is many remaining effects, then create a simultaneous choice
            if (choice == null && effectQueue.size() >= 2) {
                choice = new SimultaneousEffectsChoice(currentPlayer, new HashSet<>(effectQueue));
                effectQueue.clear();
                return;
            } else {
                refreshBoard();
            }
        }

        // Execute the after effect when queue is empty
        if (afterEffect != null) {
            afterEffect.run();
            afterEffect = null;
        }
    }

    public void lifePointLost(Player player) {
        if (player.getTeam().getLifePoints() <= 0) {
            endGame(player);
            return;
        }

        for (CardInstance card : player.getBoard()) {
            addEffectsToQueue(card, EffectTiming.LIFE_LOST);
        }

        for (CardInstance card : player.getDiscardPile()) {
            addEffectsToQueue(card, EffectTiming.LIFE_LOST);
        }
    }

    public void resetChoice() {
        this.choice = null;
    }

    public void endGame(Player loser) {
        Player winner = loser.getOpponent(players);
        System.out.printf("%s wins ; %s loses\n", winner.getName(), loser.getName());

        finished = true;
    }

    public void defeatCard(CardInstance card) {
        if (card.isStillTough()) {
            card.setStillTough(false);
        } else {
            card.getOwner().addCardToDiscardPile(card);
            addEffectsToQueue(card, EffectTiming.DEFEATED);
        }
    }

    public void refreshBoard() {
        List<EffectToApply> powerUpEffects = new ArrayList<>();
        List<EffectToApply> otherEffects = new ArrayList<>();

        for (Player player: players) {
            player.refresh();

            for (CardInstance cardInstance : player.getBoard()) {
                List<AbstractEffect> cardEffects = cardInstance.getEffects(EffectTiming.PASSIVE);
                for (AbstractEffect cardEffect : cardEffects) {
                    EffectToApply effectToApply = new EffectToApply(Collections.singletonList(cardEffect), cardInstance, this);
                    if (cardEffect.getType() == EffectType.POWER_UP) {
                        powerUpEffects.add(effectToApply);
                    } else {
                        otherEffects.add(effectToApply);
                    }
                }
            }

            for (CardInstance cardInstance : player.getDiscardPile()) {
                List<AbstractEffect> cardEffects = cardInstance.getEffects(EffectTiming.DISCARD);
                for (AbstractEffect cardEffect : cardEffects) {
                    EffectToApply effectToApply = new EffectToApply(Collections.singletonList(cardEffect), cardInstance, this);
                    if (cardEffect.getType() == EffectType.POWER_UP) {
                        powerUpEffects.add(effectToApply);
                    } else {
                        otherEffects.add(effectToApply);
                    }
                }
            }
        }

        for (EffectToApply effect : powerUpEffects) {
            effect.getEffects().forEach(effectToApply -> effectToApply.apply(this, effect.getCard()));
        }

        for (EffectToApply effect : otherEffects) {
            effect.getEffects().forEach(effectToApply -> effectToApply.apply(this, effect.getCard()));
        }
    }

    // Return the first player of the game (should only be used once per game)
    private Player getFirstPlayer() {
        List<Player> validPlayers = new ArrayList<>(players);

        System.out.println("Calcul du premier joueur :");

        while (validPlayers.size() != 1) {
            int higherPower = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                // Get a random card from the remaining cards
                CardInstance bannedCard = banCard();
                System.out.printf("\t%s %s %d\n", player.getName(), bannedCard.getCard().getName(), bannedCard.getPower());

                if (bannedCard.getPower() < higherPower) {
                    // Current player will not be the first one
                    continue;
                } else if (bannedCard.getPower() > higherPower) {
                    // Update higherPower value and clean the next players set
                    higherPower = bannedCard.getPower();
                    nextPlayers.clear();
                } else {
                    // Nothing to do, we will just add the player to the nextPlayers set
                }

                nextPlayers.add(player);
            }

            validPlayers = nextPlayers;
        }

        System.out.printf(" -> %s sera le premier joueur\n", validPlayers.getFirst().getName());

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

    @Override
    public String toString() {
        return "Game{" +
                "currentPlayer=" + currentPlayer.getName() +
                ", finished=" + finished +
                ", bannedCards=" + bannedCards +
                ", playedCard=" + playedCard +
                ", attackingCard=" + attackingCard +
                ", effectQueue=" + effectQueue +
                ", choice=" + choice +
                ", afterEffect=" + afterEffect +
                '}';
    }
}
