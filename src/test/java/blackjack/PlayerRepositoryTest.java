package blackjack;

import blackjack.model.Player;
import blackjack.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataR2dbcTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldReturnPlayersSortedByTotalPoints() {
        playerRepository.deleteAll() // Clear DB first
                .thenMany(Flux.just(
                        new Player(1, "Alice", 50, null),
                        new Player(2, "Bob", 200, null),
                        new Player(3, "Charlie", 100, null)
                ))
                .flatMap(playerRepository::save)
                .thenMany(playerRepository.getRanking()) // Fetch sorted
                .map(Player::getName)
                .as(StepVerifier::create)
                .expectNext("Bob", "Charlie", "Alice") // Check order
                .verifyComplete();
    }

    @Test
    void shouldHaveNullStatusByDefault() {
        Player player = Player.builder()
                .name("TestPlayer")
                .totalPoints(100)
                .build();
        assertNull(player.getStatus(), "Status should be null by default");
    }

}
