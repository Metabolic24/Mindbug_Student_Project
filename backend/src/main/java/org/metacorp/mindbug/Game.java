package org.metacorp.mindbug;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.choice.Choice;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;
import org.metacorp.mindbug.choice.SimultaneousChoice;
import org.metacorp.mindbug.effect.EffectToApply;
import org.metacorp.mindbug.effect.InternalEffect;
import org.metacorp.mindbug.effect.ReviveEffect;

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
    private SimultaneousChoice choice;
    private ChoiceList choiceList;

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

    // Method executed when a player plays a card, no matter how or why
    public void playCard(CardInstance card, boolean mindBug) {
        if (card == null || choice != null || choiceList != null) {
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
        }

        managePlayedCard(card, mindBug);
        resolveEffectQueue();
    }

    protected void managePlayedCard(CardInstance card, boolean mindBug) {
        // Add PLAY effects if player is allowed to trigger them
        addEffectsToQueue(card, EffectTiming.PLAY);

        // In any case, update player board
        InternalEffect playCardEffect = new InternalEffect(() -> {
            Player cardOwner = card.getOwner();
            cardOwner.addCardToBoard(card, mindBug);
            setCurrentPlayer(cardOwner.getOpponent(getPlayers()));
        });
        effectQueue.add(new EffectToApply(playCardEffect));
    }

    // Method executed when a player choose
    public void attack(CardInstance attackCard, CardInstance defendCard, Player defender) {
        if (attackCard == null || defender == null || !attackCard.isCanAttack() || (defendCard != null && !defendCard.isCanBlock())
                || choice != null || choiceList != null) {
            // TODO Throw an error as we should not be able to play a card while choice is active (same if some inputs are null)
            return;
        }

        manageAttack(attackCard, defendCard, defender);
        resolveEffectQueue();
    }

    // This method has been separated from attack one to ease unit tests
    protected void manageAttack(CardInstance attackCard, CardInstance defendCard, Player defender) {
        // Add ATTACK effects if player is allowed to trigger them
        addEffectsToQueue(attackCard, EffectTiming.ATTACK);

        InternalEffect resolveAttackEffect = new InternalEffect(() -> {
            resolveAttack(attackCard, defendCard, defender);
            // TODO Gérer le cas FRENZY avec un choice (le currentPlayer ne doit pas changer sur la première attaque)
            // TODO Trouver un moyen de compter les attaques d'un monstre FRENZY
            setCurrentPlayer(currentPlayer.getOpponent(getPlayers()));
        });
        effectQueue.add(new EffectToApply(resolveAttackEffect));
    }

    protected void resolveAttack(CardInstance attackCard, CardInstance defendCard, Player defender) {
        if (defendCard == null) {
            defender.getTeam().loseLifePoints(1);
            lifePointLost(defender);
        } else {
            if (attackCard.getPower() > defendCard.getPower()) {
                defender.addCardToDiscardPile(defendCard);

                addEffectsToQueue(defendCard, EffectTiming.DEFEATED);
            } else {
                attackCard.getOwner().addCardToDiscardPile(attackCard);

                if (attackCard.getPower() == defendCard.getPower()) {
                    defender.addCardToDiscardPile(defendCard);

                    if (attackCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
                        addEffectsToQueue(defendCard, EffectTiming.DEFEATED);
                    } else {
                        if (defendCard.getEffects(EffectTiming.DEFEATED).isEmpty()) {
                            addEffectsToQueue(attackCard, EffectTiming.DEFEATED);
                        } else {
                            choice = new SimultaneousChoice(attackCard.getOwner(), EffectTiming.DEFEATED);
                            choice.add(new Choice(attackCard, ChoiceLocation.DISCARD));
                            choice.add(new Choice(defendCard, ChoiceLocation.DISCARD));
                        }
                    }
                } else {
                    addEffectsToQueue(attackCard, EffectTiming.DEFEATED);
                }
            }
        }
    }

    public void resolveChoiceList(List<Choice> chosenCards) {
        if (choiceList == null) {
            //TODO Raise an error or log message
            return;
        }

        List<Choice> expectedChoices = choiceList.getChoices();

        for (Choice choice : chosenCards) {
            if (!expectedChoices.contains(choice)) {
                // TODO Raise an error
            }
        }

        // Update choiceList then call resolve method of the source effect
        choiceList.setChoices(chosenCards);
        choiceList.getSourceEffect().resolve(choiceList);

        // Reset choiceList value
        choiceList = null;

        resolveEffectQueue();
    }

    public void applyChoice(List<Choice> choices) {
        // Retrieve the current choice and check it exists
        if (this.choice == null || choices == null || choices.size() != this.choice.size()) {
            //TODO Raise an error
            return;
        }

        resolveSimultaneousChoice(choices);

        resolveEffectQueue();
    }

    /**
     * Solve a pending choice
     *
     * @param choices the list of ordered choices to apply
     */
    protected void resolveSimultaneousChoice(List<Choice> choices) {
        // Process the incoming choice list
        for (Choice choice : choices) {
            if (this.choice.contains(choice)) {
                addEffectsToQueue(choice.getCard(), this.choice.getEffectTiming());
            } else {
                //TODO Raise an error
            }
        }

        // Reset the choice only if the given choice list was valid
        this.choice = null;
    }

    public void addEffectsToQueue(CardInstance card, EffectTiming timing) {
        if (card.getOwner().canTrigger(timing)) {
            effectQueue.addAll(card.getEffects(timing).stream()
                    .map(effect -> new EffectToApply(effect, card, this))
                    .toList());
        }
    }

    public void resolveEffectQueue() {
        // TODO Maybe raise an error at the start if a choice is remaining

        while (choice == null && choiceList == null && !effectQueue.isEmpty()) {
            EffectToApply currentEffect = effectQueue.peek();
            currentEffect.getEffect().apply(this, currentEffect.getCard());
            // TODO Faut-il envisager un système transactionnel ou similaire pour gérer le fait qu'une application d'effet puisse échouer sans pour autant dégrader l'état actuel du jeu?
            // Only remove effect from queue after it is applied to avoid loss of data
            effectQueue.remove(currentEffect);
        }
    }

    public void lifePointLost(Player player) {
        if (player.getTeam().getLifePoints() <= 0) {
            endGame(player);
            return;
        }

        for (CardInstance card : player.getDiscardPile()) {
            List<Effect> effects = card.getEffects(EffectTiming.PASSIVE).stream().filter(effect -> effect instanceof ReviveEffect).toList();
            if (!effects.isEmpty()) {
                // We consider that, for the moment, it is not necessary to create a simultaneous choice here even if there are multiple cards to revive
                effectQueue.add(new EffectToApply(effects.getFirst(), card, this));
            }
        }
    }

    private void endGame(Player loser) {
        Player winner = loser.getOpponent(players);

        System.out.printf("%s wins ; %s loses", winner.getName(), loser.getName());
    }

    // Return the first player of the game (should only be used once per game)
    private Player getFirstPlayer() {
        List<Player> validPlayers = new ArrayList<>(players);
        while (validPlayers.size() != 1) {
            int power = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                CardInstance firstPlayerCard = banCard();

                if (firstPlayerCard.getPower() < power) {
                    continue;
                } else if (firstPlayerCard.getPower() > power) {
                    power = firstPlayerCard.getPower();
                    nextPlayers.clear();
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
