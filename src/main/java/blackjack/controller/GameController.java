package blackjack.controller;

import blackjack.model.dto.GameDTO;
import blackjack.model.dto.mapDTO;
import blackjack.model.enums.MoveType;
import blackjack.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public Mono<ResponseEntity<GameDTO>> createGame(@RequestBody String playerName) {
        return gameService.createGame(playerName)
                .map(mapDTO::toGameDTO)
                .map(gameDTO -> ResponseEntity.status(HttpStatus.CREATED).body(gameDTO))
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get game by ID", description = "Returns the details of a game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game details found"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public Mono<ResponseEntity<GameDTO>> getGame(@PathVariable String gameId) {
        if (gameId == null || gameId.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return gameService.getGameById(gameId)
                .map(mapDTO::toGameDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{gameId}/move")
    public Mono<ResponseEntity<GameDTO>> makeMove(@PathVariable String gameId, @RequestBody String move) {
        MoveType moveType = parseMoveType(move);
        if (moveType == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return gameService.playerMove(gameId, moveType)
                .map(mapDTO::toGameDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    private MoveType parseMoveType(String move) {
        if (move == null || move.isBlank()) return null;
        try {
            return MoveType.valueOf(move.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Operation(summary = "Get game result", description = "Returns the result of the game and final scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game result found"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{gameId}/result")
    public Mono<ResponseEntity<GameDTO>> getWinner(@PathVariable String gameId) {
        return gameService.getGameById(gameId)
                .flatMap(gameService::finishGame)
                .map(mapDTO::toGameDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @DeleteMapping("/{gameId}/delete")
    public Mono<ResponseEntity<Object>> deleteGame(@PathVariable String gameId) {
        return gameService.deleteGame(gameId)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

}
