package perczynski.kamil.evolution.gameservice.domain.rounds;

import perczynski.kamil.evolution.gameservice.libs.Money;

public record Win(Money payout, boolean freeRound) {
}
