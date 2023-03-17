package perczynski.kamil.evolution.gameservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import perczynski.kamil.evolution.gameservice.domain.GameService;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundListing;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundService;
import perczynski.kamil.evolution.gameservice.libs.Money;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GameController {

    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final GameRoundService gameRoundService;

    @PostMapping("/players")
    public Player registerPlayer() {
        return gameService.registerPlayer();
    }

    @PostMapping("/players/{playerId}/bets")
    public GameRound placeBet(@PathVariable String playerId,
                              @RequestBody @Valid PlaceBetRequest placeBetRequest) {
        return gameService.placeBet(
                playerId,
                Bet.builder()
                        .mode(placeBetRequest.mode())
                        .stake(new Money(placeBetRequest.stake()))
                        .build()
        );
    }

    @GetMapping("/game-rounds/{roundId}")
    public GameRound readGameRound(@PathVariable String roundId) {
        log.debug("Reading game round by id: {}", roundId);
        return gameRoundService.readGameRound(roundId);
    }

    @GetMapping("/game-rounds")
    public GameRoundListing listRounds(@RequestParam String playerId) {
        log.debug("Listing game rounds for player: {}", playerId);
        return gameRoundService.listGameRounds(playerId);
    }

}
