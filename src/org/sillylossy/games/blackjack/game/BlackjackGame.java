package org.sillylossy.games.blackjack.game;

import org.sillylossy.games.blackjack.players.Dealer;
import org.sillylossy.games.common.Main;
import org.sillylossy.games.common.cards.Card;
import org.sillylossy.games.common.cards.Deck;
import org.sillylossy.games.common.game.BetGame;
import org.sillylossy.games.common.game.CardGame;
import org.sillylossy.games.common.game.StatEvent;

/**
 * Blackjack game model.
 */
public class BlackjackGame extends CardGame implements BetGame {

    /**
     * Name of the game.
     */
    public static final String GAME_NAME = "Blackjack";

    /**
     * Returns a numeric value that represents a hand's value.
     * Cards with number have value of that number. Cards with picture (jack, queen, king)
     * have 10 points value. Ace is valued 1 or 11 (as player wants).
     */
    public int getValue(Card[] hand) {
        int i = 0;
        int j = 0;
        for (Card card : hand) {
            switch (card.getCardFace()) {
                case ACE:
                    i += 11;
                    j += 1;
                    break;
                case EIGHT:
                    i += 8;
                    j += 8;
                    break;
                case FIVE:
                    i += 5;
                    j += 5;
                    break;
                case FOUR:
                    i += 4;
                    j += 4;
                    break;
                case JACK:
                    i += 10;
                    j += 10;
                    break;
                case KING:
                    i += 10;
                    j += 10;
                    break;
                case NINE:
                    i += 9;
                    j += 9;
                    break;
                case QUEEN:
                    i += 10;
                    j += 10;
                    break;
                case SEVEN:
                    i += 7;
                    j += 7;
                    break;
                case SIX:
                    i += 6;
                    j += 6;
                    break;
                case TEN:
                    i += 10;
                    j += 10;
                    break;
                case THREE:
                    i += 3;
                    j += 3;
                    break;
                case TWO:
                    i += 2;
                    j += 2;
                    break;
                default:
                    break;
            }
        }
        return i > BlackjackGame.BLACKJACK ? j : i;
    }

    /**
     * Determines whether a player has "blackjack" (2 cards, total 21 value).
     */
    private boolean isBlackJack(Card[] hand) {
        return hand.length == 2 && getValue(hand) == BLACKJACK;
    }

    /**
     * How many value points needed for "blackjack".
     */
    private final static int BLACKJACK = 21;

    /**
     * A dealer assigned to a game instance.
     */
    private final Dealer dealer = new Dealer();

    /**
     * Performs a bet game event. Deals cards to players and sets bet to player account.
     *
     * @param bet bet amount
     */
    @Override
    public void betAction(int bet) {
        dealCards();
        player.setBet(bet);
    }

    /**
     * Performs a double game event. If player has sufficient score, double's his bet and performs hit event.
     *
     * @return taken card or null if player has insufficient score
     */
    public Card doubleAction() {
        int newBet = player.getBet() * 2;
        if (player.getScore() < newBet) {
            return null;
        }
        player.setBet(newBet);
        return hitAction();
    }

    /**
     * Gets maximum bet (half of player's score).
     */
    @Override
    public int getMaxBet() {
        return player.getScore() / 2;
    }

    /**
     * Gets minimum bet(twentieth part of player's score).
     */
    @Override
    public int getMinBet() {
        return player.getScore() / 20;
    }

    /**
     * Gets game's dealer.
     */
    public Dealer getDealer() {
        return dealer;
    }

    /**
     * Performs a hit game action. Draws a card, adds it to a hand and returns that card.
     *
     * @return taken card
     */
    public Card hitAction() {
        Card card = deck.draw();
        player.getHand().addCard(card);
        return card;
    }

    @Override
    public String getResult() {
        String result = "Can't identify result";
        int bet = player.getBet();
        Card[] playerCards = player.getHand().getCards();
        Card[] dealerCards = dealer.getHand().getCards();
        int dealerValue = getValue(dealerCards);
        int playerValue = getValue(playerCards);
        boolean playerBlackjack = isBlackJack(playerCards);
        boolean dealerBlackjack = isBlackJack(dealerCards);
        if (!playerBlackjack && dealerBlackjack) {
            player.decreaseScore(bet);
            Main.getGameController().addStatEvent(player, StatEvent.LOST);
            result = "You've lost. Dealer has blackjack";
        } else if (playerBlackjack && dealerBlackjack) {
            Main.getGameController().addStatEvent(player, StatEvent.PUSH);
            result = "Push. Dealer has blackjack too";
        } else if (playerBlackjack) {
            player.increaseScore(Math.round(bet * 1.5f));
            Main.getGameController().addStatEvent(player, StatEvent.WON);
            result = "You've won: blackjack";
        } else if (playerValue > BLACKJACK) {
            player.decreaseScore(bet);
            Main.getGameController().addStatEvent(player, StatEvent.LOST);
            result = "You've lost: overtake";
        } else if (dealerValue > BLACKJACK && playerValue <= BLACKJACK) {
            player.increaseScore(bet);
            Main.getGameController().addStatEvent(player, StatEvent.WON);
            result = "You've won: dealer overtake";
        } else if (playerValue > dealerValue) {
            player.increaseScore(bet);
            Main.getGameController().addStatEvent(player, StatEvent.WON);
            result = "You've won: you have more points than dealer";
        } else if (playerValue == dealerValue) {
            Main.getGameController().addStatEvent(player, StatEvent.PUSH);
            result = "Push. Your points with dealer are equal.";
        } else if (playerValue < dealerValue) {
            player.decreaseScore(player.getBet());
            Main.getGameController().addStatEvent(player, StatEvent.LOST);
            result = "You've lost: dealer has more points (" + dealerValue + ")";
        }
        reset();
        Main.saveData();
        return result;
    }

    /**
     * Clears dealer's and player's hand, sets stand flag to false and bet to 0.
     */
    @Override
    protected void reset() {
        player.getHand().clear();
        player.setBet(0);
        player.setStand(false);
        dealer.getHand().clear();
    }

    @Override
    public boolean shouldEnd() {
        Card[] playerCards = player.getHand().getCards();
        if (isBlackJack(playerCards)) {
            return true;
        } else if (getValue(playerCards) > BLACKJACK) {
            return true;
        } else if (player.isStand()) {
            return true;
        }
        return false;
    }

    @Override
    protected void dealCards() {
        setDeck(Deck.create());
        dealer.getHand().addCard(deck.draw());
        dealer.getHand().addCard(deck.draw());
        player.getHand().addCard(deck.draw());
        player.getHand().addCard(deck.draw());
    }

    @Override
    public String getGameName() {
        return GAME_NAME;
    }

    /**
     * Performs a stand action.
     */
    public void standAction() {
        player.setStand(true);
    }

}
