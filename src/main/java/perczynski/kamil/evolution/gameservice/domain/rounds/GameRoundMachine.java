package perczynski.kamil.evolution.gameservice.domain.rounds;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.libs.Money;

@Component
@RequiredArgsConstructor
public class GameRoundMachine {

    private final GameRoundMachineProperties gameRoundMachineProperties;
    private final RandomFacts randomFacts;

    public Win drawWin(Bet bet) {
        final Money money = drawMoneyWin(bet);
        final boolean isNextRoundFree = randomFacts.drawFreeNextRound();

        return new Win(money, isNextRoundFree);
    }

    private Money drawMoneyWin(Bet bet) {
        if (!randomFacts.drawMoneyWin()) {
            return Money.ZERO;
        }

        final int multiplier = drawPayoutMultiplier();
        return bet.stake().multiply(multiplier);
    }

    private int drawPayoutMultiplier() {
        if (randomFacts.drawBigWin()) {
            return gameRoundMachineProperties.bigWinMultiplier();
        }
        if (randomFacts.drawMediumWin()) {
            return gameRoundMachineProperties.mediumWinMultiplier();
        }
        return gameRoundMachineProperties.smallWinMultiplier();
    }

}
