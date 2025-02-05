package blackjack.model.dto;

import blackjack.model.Card;
import blackjack.model.enums.GameStatus;
import blackjack.model.enums.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO {
    private String id;
    private PlayerDTO playerDTO;
    private Deque<Card> playerCards;
    private Deque<Card> dealerCards;
    private GameStatus gameStatus;
    private Participant winner;
}
