package perczynski.kamil.evolution.gameservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import perczynski.kamil.evolution.gameservice.domain.GameService;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;
import perczynski.kamil.evolution.gameservice.libs.Money;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GameController {

    private final GameService gameService;
    private final PlayerRepository playerRepository;

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

    @GetMapping("/players/{playerId}")
    public Player readPlayer(@PathVariable String playerId) {
        return playerRepository.find(playerId);
    }

}