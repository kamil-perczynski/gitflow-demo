package perczynski.kamil.evolution.gameservice.api;

import lombok.Builder;
import perczynski.kamil.evolution.gameservice.domain.bets.BetMode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
public record PlaceBetRequest(
        @Min(100)
        @Max(1000)
        int stake,

        @NotNull BetMode mode
) {
}