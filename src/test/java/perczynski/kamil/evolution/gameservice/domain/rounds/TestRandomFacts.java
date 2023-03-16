package perczynski.kamil.evolution.gameservice.domain.rounds;

import lombok.Builder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class TestRandomFacts extends RandomFacts {

    private Config config = Config.builder().build();

    public void fixFacts(Config config) {
        this.config = config;
    }

    @Override
    public boolean drawMoneyWin() {
        return config.moneyWin();
    }

    @Override
    public boolean drawFreeNextRound() {
        return config.freeNextRound();
    }

    @Override
    public boolean drawMediumWin() {
        return config.mediumWin();
    }

    @Override
    public boolean drawBigWin() {
        return config.bigWin();
    }

    @Builder(toBuilder = true)
    public record Config(
            boolean moneyWin,
            boolean freeNextRound,
            boolean mediumWin,
            boolean bigWin
    ) {
    }
}