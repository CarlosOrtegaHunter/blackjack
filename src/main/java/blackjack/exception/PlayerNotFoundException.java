package blackjack.exception;

import blackjack.service.PlayerService;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String playerName) {
        super("Player " + playerName + " not found. ");
    }
    public PlayerNotFoundException(Integer playerId) {
        super("Player with ID " + playerId + " not found. ");
    }

    public PlayerNotFoundException() {
        super("Player not found. ");
    }
}