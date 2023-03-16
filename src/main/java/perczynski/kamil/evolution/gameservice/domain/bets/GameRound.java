package perczynski.kamil.evolution.gameservice.domain.bets;

import lombok.Builder;
import perczynski.kamil.evolution.gameservice.domain.rounds.Win;
import perczynski.kamil.evolution.gameservice.libs.Money;

@Builder
public record GameRound(
        String id,
        Bet bet,
        Win win,
        Money nextBalance,
        String playerId
) {
}
