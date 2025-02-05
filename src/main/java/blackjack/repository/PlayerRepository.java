package blackjack.repository;

import blackjack.model.Player;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Integer> {
    Mono<Player> findByName(String name);
    Mono<Player> findById(int id);

    @Query("SELECT * FROM player ORDER BY total_points DESC")
    Flux<Player> getRanking();
}
