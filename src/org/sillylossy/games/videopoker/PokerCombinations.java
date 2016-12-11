package org.sillylossy.games.videopoker;

import org.sillylossy.games.common.cards.Card;
import org.sillylossy.games.common.cards.CardRank;
import org.sillylossy.games.common.cards.CardSuit;

import java.util.*;

class PokerCombinations {

    private static final int PAIR_REPEATS = 1;

    private static final int THREE_REPEATS = 2;

    private static final int FOUR_REPEATS = 3;
    private static Map<CardRank, Integer> aceIsLowValues = getAceIsLowValues();
    private static Comparator<Card> aceIsLowComparator = new Comparator<Card>() {
        @Override
        public int compare(Card o1, Card o2) {
            return Integer.compare(aceIsLowValues.get(o1.getCardRank()), aceIsLowValues.get(o2.getCardRank()));
        }
    };
    private static Map<CardRank, Integer> aceIsHighValues = getAceIsHighValues();
    private static Comparator<Card> aceIsHighComparator = new Comparator<Card>() {
        @Override
        public int compare(Card o1, Card o2) {
            return Integer.compare(aceIsHighValues.get(o1.getCardRank()), aceIsHighValues.get(o2.getCardRank()));
        }
    };
    private int numberOfRepeats;
    private EnumSet<CardRank> rankEnumSet;
    private Card[] cards;

    private PokerCombinations(int repeats, EnumSet<CardRank> set, Card[] cards) {
        numberOfRepeats = repeats;
        rankEnumSet = set;
        this.cards = cards;
    }

    private static Map<CardRank, Integer> getAceIsLowValues() {
        Map<CardRank, Integer> values = new HashMap<>(CardRank.values().length);
        putCommonValues(values);
        values.put(CardRank.ACE, 1);
        return values;
    }

    private static void putCommonValues(Map<CardRank, Integer> map) {
        map.put(CardRank.TWO, 2);
        map.put(CardRank.THREE, 3);
        map.put(CardRank.FOUR, 4);
        map.put(CardRank.FIVE, 5);
        map.put(CardRank.SIX, 6);
        map.put(CardRank.SEVEN, 7);
        map.put(CardRank.EIGHT, 8);
        map.put(CardRank.NINE, 9);
        map.put(CardRank.TEN, 10);
        map.put(CardRank.JACK, 11);
        map.put(CardRank.QUEEN, 12);
        map.put(CardRank.KING, 13);
    }

    private static Map<CardRank, Integer> getAceIsHighValues() {
        Map<CardRank, Integer> values = new HashMap<>(CardRank.values().length);
        putCommonValues(values);
        values.put(CardRank.ACE, 14);
        return values;
    }

    static PokerCombinations getCombinations(Card[] cards) {
        int repeats = 0;
        EnumSet<CardRank> pairsSet = EnumSet.noneOf(CardRank.class);
        Arrays.sort(cards, aceIsHighComparator);
        for (int i = 0; i < cards.length - 1; i++) {
            if (cards[i].getCardRank() == cards[i + 1].getCardRank()) {
                repeats++;
                pairsSet.add(cards[i].getCardRank());
            }
        }
        return new PokerCombinations(repeats, pairsSet, cards);
    }

    boolean hasStraight() {
        return hasStraight(true) || hasStraight(false);
    }

    boolean hasStraightFlush() {
        return hasFlush() && hasStraight();
    }

    boolean hasRoyalStraightFlush() {
        return hasFlush() && hasStraight(true) && cards[0].getCardRank() == CardRank.TEN;
    }

    private boolean hasStraight(boolean aceIsHigh) {
        Arrays.sort(cards, aceIsHigh ? aceIsHighComparator : aceIsLowComparator);
        Map<CardRank, Integer> values = aceIsHigh ? aceIsHighValues : aceIsLowValues;
        for (int i = 0; i < cards.length - 1; i++) {
            if (values.get(cards[i].getCardRank()) > values.get(cards[i + 1].getCardRank())) {
                return false;
            }
        }
        return true;
    }

    boolean hasFlush() {
        CardSuit flushSuit = cards[0].getCardSuit();
        for (Card card : cards) {
            if (flushSuit != card.getCardSuit()) {
                return false;
            }
        }
        return true;
    }

    boolean hasOnePair() {
        return numberOfRepeats == PAIR_REPEATS && rankEnumSet.size() == 1;
    }

    boolean hasTwoPair() {
        return numberOfRepeats == THREE_REPEATS && rankEnumSet.size() == 2;
    }

    boolean hasThree() {
        return numberOfRepeats == THREE_REPEATS && rankEnumSet.size() == 1;
    }

    boolean hasFour() {
        return numberOfRepeats == FOUR_REPEATS && rankEnumSet.size() == 1;
    }

    boolean hasFullHouse() {
        return numberOfRepeats == FOUR_REPEATS && rankEnumSet.size() == 2;
    }

}