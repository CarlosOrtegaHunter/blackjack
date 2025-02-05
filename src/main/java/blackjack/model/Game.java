package blackjack.model;

import blackjack.model.enums.GameStatus;
import blackjack.model.enums.Participant;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Deque;
import java.util.ArrayDeque;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "games") // MongoDB collection name
public class Game {

    @Id
    private String id;

    private Player player;

    private Deque<Card> playerCards;

    private Deque<Card> dealerCards;

    private GameStatus gameStatus;

    private Participant winner;

    private Card hiddenCard;

    private Deque<Card> deck;

}
