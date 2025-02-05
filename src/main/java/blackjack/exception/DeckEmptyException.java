package blackjack.exception;

public class DeckEmptyException extends RuntimeException {
    public DeckEmptyException(String deckIsEmpty) {
        super(deckIsEmpty);
    }
}
