package perczynski.kamil.evolution.gameservice.domain.bets;

import lombok.Builder;
import perczynski.kamil.evolution.gameservice.libs.Money;

@Builder
public record Bet(BetMode mode, Money stake) {
}