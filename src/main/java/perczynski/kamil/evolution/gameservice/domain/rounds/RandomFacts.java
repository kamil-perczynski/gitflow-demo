package perczynski.kamil.evolution.gameservice.domain.rounds;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomFacts {

    private final Random random = new Random();

    public boolean drawMoneyWin() {
        return random.nextDouble() < .3;
    }

    public boolean drawFreeNextRound() {
        return random.nextDouble() < .1;
    }

    public boolean drawMediumWin() {
        return random.nextDouble() < .1;
    }

    public boolean drawBigWin() {
        return random.nextDouble() < .05;
    }

}
