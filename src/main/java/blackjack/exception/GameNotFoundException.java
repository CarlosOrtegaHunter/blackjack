package blackjack.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameId) {
        super("Game " + gameId + " not found. ");
    }
}
