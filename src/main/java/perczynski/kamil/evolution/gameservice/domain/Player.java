package perczynski.kamil.evolution.gameservice.domain;

import lombok.Builder;
import perczynski.kamil.evolution.gameservice.libs.Money;

import java.util.StringJoiner;

@Builder(toBuilder = true)
public record Player(
        String playerId,
        Money balance,
        boolean freeRoundAvailable
) {
    @Override
    public String toString() {
        return new StringJoiner(", ", Player.class.getSimpleName() + "[", "]")
                .add("playerId='" + playerId + "'")
                .add("balance=***")
                .add("freeRoundAvailable=" + freeRoundAvailable)
                .toString();
    }
}
