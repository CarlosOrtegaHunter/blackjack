package blackjack.model.dto;

import blackjack.model.Game;
import blackjack.model.Player;

public class mapDTO {

    public static GameDTO toGameDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .playerDTO(toPlayerDTO(game.getPlayer()))
                .playerCards(game.getPlayerCards())
                .dealerCards(game.getDealerCards())
                .gameStatus(game.getGameStatus())
                .build();
    }

    private static PlayerDTO toPlayerDTO(Player playerPlaying) {
        return PlayerDTO.builder()
                .id(playerPlaying.getId())
                .name(playerPlaying.getName())
                .totalPoints(playerPlaying.getTotalPoints())
                .status(playerPlaying.getStatus())
                .build();
    }
}
