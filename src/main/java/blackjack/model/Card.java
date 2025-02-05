package blackjack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@ToString
@AllArgsConstructor
public class Card {

    public enum Suit {
        SPADE, DIAMOND, HEART, CLUB
    }

    @Getter
    @AllArgsConstructor
    public enum Value {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(10), QUEEN(10), KING(10), ACE(1, 11);

        private final int minValue;
        private final int maxValue;

        // Overloaded constructor for non-ace
        Value(int value) {
            this(value, value);
        }

        public boolean isAce() {
            return minValue != maxValue;
        }
    }

    private final Suit suit;
    private final Value value;

    public static List<Card> generateDeck() {
        List<Card> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Value value : Value.values()) {
                deck.add(new Card(suit, value));
            }
        }
        return deck;
    }
}