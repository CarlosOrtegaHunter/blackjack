package blackjack.controller;

import blackjack.model.Player;
import blackjack.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/new")
    @Operation(summary = "Create a new player", description = "Adds a new player to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid player name")
    })
    public Mono<ResponseEntity<Player>> createPlayer(@RequestBody String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return playerService.createPlayer(playerName)
                .map(savedPlayer -> ResponseEntity.status(201).body(savedPlayer))
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{playerId}")
    @Operation(summary = "Get player by ID", description = "Returns player details for the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player found"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<Player>> getPlayer(@PathVariable int playerId) {
        return playerService.getPlayerById(playerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking")
    @Operation(summary = "Get player ranking", description = "Returns a list of players sorted by total points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No players in the ranking")
    })
    public Mono<ResponseEntity<Flux<Player>>> getRanking() {
        return playerService.getRanking()
                .collectList()
                .map(players -> players.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(Flux.fromIterable(players)));
    }

    @PutMapping("/{playerId}/name")
    @Operation(summary = "Update player name", description = "Updates the name of the player with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player name updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid new player name"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<Player>> updateName(@PathVariable int playerId, @RequestBody String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return playerService.updateName(playerId, newName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
