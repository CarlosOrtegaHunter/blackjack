package blackjack.service;

import blackjack.exception.PlayerNotFoundException;
import blackjack.model.Player;
import blackjack.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Mono<Player> createPlayer(String name) {
        return playerRepository.findByName(name)
                .flatMap(existingPlayer -> Mono.error(new IllegalArgumentException("Player with name '" + name + "' already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    Player player = Player.builder()
                            .name(name)
                            .totalPoints(0)
                            .build();
                    return playerRepository.save(player);
                }))
                .cast(Player.class);
    }

    public Mono<Player> getPlayerById(int playerId) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)));
    }

    public Mono<Player> addPoints(int playerId, int points) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setTotalPoints(player.getTotalPoints() + points);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)));
    }

    public Mono<Player> updateName(int playerId, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Player name cannot be null or empty"));
        }
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(player -> playerRepository.findByName(newName)
                        .flatMap(existing -> Mono.error(new IllegalArgumentException("Player with name '" + newName + "' already exists")))
                        .switchIfEmpty(Mono.defer(() -> {
                            player.setName(newName);
                            return playerRepository.save(player);
                        }))
                        .cast(Player.class)
                );
    }

    public Flux<Player> getRanking() {
        return playerRepository.getRanking()
                .switchIfEmpty(Flux.error(new PlayerNotFoundException("No players found")));
    }
}
