package blackjack.service;

import blackjack.exception.DeckEmptyException;
import blackjack.exception.GameNotFoundException;
import blackjack.model.Card;
import blackjack.model.Game;
import blackjack.model.Player;
import blackjack.model.enums.MoveType;
import blackjack.model.enums.GameStatus;
import blackjack.model.enums.Participant;
import blackjack.model.enums.PlayerStatus;
import blackjack.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final Random random = new Random();
    private final PlayerService playerService;

    private final List<Card> DECK = Card.generateDeck();

    private Card drawCard(Deque<Card> deck) {
        if (deck.isEmpty()) {
            throw new DeckEmptyException("The deck is empty");
        }
        return deck.pop();
    }

    public Mono<Game> createGame(String playerName) {

        Deque<Card> deck = new ArrayDeque<>(DECK);
        Collections.shuffle((List<Card>) deck, random);

        Deque<Card> playerCards = new ArrayDeque<>(List.of(drawCard(deck), drawCard(deck)));
        Deque<Card> dealerCards = new ArrayDeque<>(List.of(drawCard(deck)));

        return createPlayer(playerName)
                .map(player -> Game.builder()
                        .player(player)
                        .winner(Participant.NONE)
                        .gameStatus(GameStatus.ACTIVE)
                        .playerCards(playerCards)
                        .dealerCards(dealerCards)
                        .hiddenCard(drawCard(deck))
                        .deck(deck)
                        .build()).flatMap(gameRepository::save);
    }

    public Mono<Player> createPlayer(String name) {
        return playerService.createPlayer(name);
    }

    public Mono<Game> getGameById(String gameId) {
        return gameRepository.findById(gameId);
    }

    public Mono<Void> deleteGame(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException(id)))
                .flatMap(gameRepository::delete);
    }

    public Mono<Game> playerMove(String gameId, MoveType move) {
        return gameRepository.findById(gameId)
                .flatMap(game -> switch (move) {
                    case STAND -> handleDealerTurn(game).flatMap(this::finishGame);
                    case HIT -> addCardToPlayer(game);
                });
    }

    private Mono<Game> addCardToPlayer(Game game) {
        if (game.getGameStatus() != GameStatus.ACTIVE) {
            return Mono.just(game);
        }
        if (game.getDeck() == null || game.getDeck().isEmpty()) {
            return Mono.error(new DeckEmptyException("Player can't draw cards from an empty deck"));
        }

        game.getPlayerCards().add(drawCard(game.getDeck()));
        Game updatedGame = game;
        // Player busts
        if (calculateHandScore(game.getPlayerCards()) > 21) {
            game.getPlayer().setStatus(PlayerStatus.LOST);
            playerService.addPoints(game.getPlayer().getId(), -2);
            updatedGame = game.toBuilder()
                    .gameStatus(GameStatus.FINISHED)
                    .winner(Participant.DEALER)
                    .build();
        }

        return gameRepository.save(updatedGame);
    }

    private Mono<Game> handleDealerTurn(Game game) {
        try{
            if (game.getGameStatus() != GameStatus.ACTIVE) {
                return Mono.just(game);
            }
            game.getDealerCards().add(game.getHiddenCard()); // reveal hidden card

            while (calculateHandScore(game.getDealerCards()) < 17 && !game.getDeck().isEmpty()) {
                game.getDealerCards().add(drawCard(game.getDeck()));
            }

            game.setGameStatus(GameStatus.FINISHED);
        }
        catch(DeckEmptyException e){
            System.out.println("Dealer can't draw cards from an empty deck");
        }
        return gameRepository.save(game);
    }

    private int calculateHandScore(Deque<Card> cards) {
        int score = 0;
        int aces = 0;

        for (Card card : cards) {
            var value = card.getValue();
            if(value.isAce()) aces++;
            score += value.getMaxValue();
        }
        while (score > 21 && aces-- > 0) score -= 10;
        return score;
    }

    public Mono<Game> finishGame(Game game) {
        int playerScore = calculateHandScore(game.getPlayerCards());
        int dealerScore = calculateHandScore(game.getDealerCards());

        PlayerStatus playerStatus;
        int points;
        Participant winner;

        if (playerScore > 21) {
            playerStatus = PlayerStatus.LOST;
            points = -2;
            winner = Participant.DEALER;
        } else if (dealerScore > 21) {
            playerStatus = PlayerStatus.WON;
            points = 2;
            winner = Participant.PLAYER;
        } else if (playerScore > dealerScore) {
            playerStatus = PlayerStatus.WON;
            points = 2;
            winner = Participant.PLAYER;
        } else if (playerScore < dealerScore) {
            playerStatus = PlayerStatus.LOST;
            points = -2;
            winner = Participant.DEALER;
        } else {
            playerStatus = PlayerStatus.TIE;
            points = 1;
            winner = Participant.NONE;
        }

        Game updatedGame = game.toBuilder()
                .gameStatus(GameStatus.FINISHED)
                .winner(winner)
                .build();

        game.getPlayer().setStatus(playerStatus);

        return playerService.addPoints(updatedGame.getPlayer().getId(), points)
                .then(gameRepository.save(updatedGame));
    }
}