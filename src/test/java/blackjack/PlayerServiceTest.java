package blackjack;
import blackjack.exception.PlayerNotFoundException;
import blackjack.model.Player;
import blackjack.model.enums.PlayerStatus;
import blackjack.repository.PlayerRepository;
import blackjack.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    private PlayerRepository playerRepository;
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerRepository = Mockito.mock(PlayerRepository.class);
        playerService = new PlayerService(playerRepository);
    }

    @Test
    void createPlayerSuccess() {
        when(playerRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(new Player(1, "John", 0, null)));

        Player player = playerService.createPlayer("John").block();

        assertNotNull(player);
        assertEquals("John", player.getName());
        assertEquals(0, player.getTotalPoints());
        assertNull(player.getStatus());

        verify(playerRepository).findByName("John");
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayerDuplicateName() {
        when(playerRepository.findByName("John")).thenReturn(Mono.just(new Player(1, "John", 0, null)));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                playerService.createPlayer("John").block()
        );

        assertEquals("Player with name 'John' already exists", exception.getMessage());

        verify(playerRepository).findByName("John");
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getPlayerByIdSuccess() {
        when(playerRepository.findById(1)).thenReturn(Mono.just(new Player(1, "John", 0, null)));

        Player player = playerService.getPlayerById(1).block();

        assertNotNull(player);
        assertEquals(1, player.getId());
        assertEquals("John", player.getName());
        assertNull(player.getStatus());

        verify(playerRepository).findById(1);
    }

    @Test
    void getPlayerByIdNotFound() {
        when(playerRepository.findById(1)).thenReturn(Mono.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () ->
                playerService.getPlayerById(1).block()
        );

        assertEquals("Player with ID 1 not found", exception.getMessage());

        verify(playerRepository).findById(1);
    }

    @Test
    void addPointsSuccess() {
        Player existingPlayer = new Player(1, "John", 10, null);
        when(playerRepository.findById(1)).thenReturn(Mono.just(existingPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(new Player(1, "John", 15, null)));

        Player updatedPlayer = playerService.addPoints(1, 5).block();

        assertNotNull(updatedPlayer);
        assertEquals(15, updatedPlayer.getTotalPoints());

        verify(playerRepository).findById(1);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void addPointsNotFound() {
        when(playerRepository.findById(1)).thenReturn(Mono.empty());

        Exception exception = assertThrows(PlayerNotFoundException.class, () ->
                playerService.addPoints(1, 5).block()
        );

        assertEquals("Player with ID 1 not found", exception.getMessage());

        verify(playerRepository).findById(1);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void updateNameSuccess() {
        Player existingPlayer = new Player(1, "John", 10, null);
        when(playerRepository.findById(1)).thenReturn(Mono.just(existingPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(new Player(1, "Doe", 10, null)));

        Player updatedPlayer = playerService.updateName(1, "Doe").block();

        assertNotNull(updatedPlayer);
        assertEquals("Doe", updatedPlayer.getName());

        verify(playerRepository).findById(1);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void updateNameInvalidInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                playerService.updateName(1, "").block()
        );

        assertEquals("Player name cannot be null or empty", exception.getMessage());

        verify(playerRepository, never()).findById(anyInt());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getRankingSuccess() {
        when(playerRepository.getRanking()).thenReturn(Flux.just(
                new Player(1, "Alice", 100, null),
                new Player(2, "Bob", 50, null)
        ));

        Flux<Player> rankingFlux = playerService.getRanking();
        Player firstPlayer = rankingFlux.blockFirst();
        Player secondPlayer = rankingFlux.blockLast();

        assertNotNull(firstPlayer);
        assertEquals("Alice", firstPlayer.getName());
        assertEquals(100, firstPlayer.getTotalPoints());

        assertNotNull(secondPlayer);
        assertEquals("Bob", secondPlayer.getName());
        assertEquals(50, secondPlayer.getTotalPoints());

        verify(playerRepository).getRanking();
    }
}
