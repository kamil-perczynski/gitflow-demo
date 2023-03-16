package perczynski.kamil.evolution.gameservice.domain.bets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.libs.ErrorCodeException;
import perczynski.kamil.evolution.gameservice.libs.Money;

import static perczynski.kamil.evolution.gameservice.domain.GameErrorCode.INSUFFICIENT_FUNDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetOperations {

    private final PlayerRepository playerRepository;

    public synchronized void tryPlaceBet(String playerId, Bet bet) {
        log.debug("Trying to place bet: {}", bet);
        final Player player = playerRepository.find(playerId);
        final Money betPlacementCost = calcBetPlacementCost(bet, player);

        if (betPlacementCost.isGreater(player.balance())) {
            throw new ErrorCodeException(INSUFFICIENT_FUNDS);
        }

        log.debug("Locking bet placement cost {} for player: {}", betPlacementCost, playerId);
        playerRepository.save(
                player.toBuilder()
                        .balance(player.balance().minus(betPlacementCost))
                        .build()
        );
    }

    private static Money calcBetPlacementCost(Bet bet, Player player) {
        if (player.freeRoundAvailable() || bet.mode() == BetMode.PLAY_FOR_FREE) {
            return Money.ZERO;
        }

        return bet.stake();
    }

}
