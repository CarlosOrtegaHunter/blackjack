package blackjack.repository;

import blackjack.model.Game;
import com.mongodb.lang.NonNull;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface GameRepository extends ReactiveMongoRepository<Game, String> {
    @NonNull
    Mono<Game> findById(@NonNull String gameId);
}