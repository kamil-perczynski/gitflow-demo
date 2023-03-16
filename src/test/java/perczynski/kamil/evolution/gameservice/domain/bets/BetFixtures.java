package perczynski.kamil.evolution.gameservice.domain.bets;

import perczynski.kamil.evolution.gameservice.libs.Money;

public class BetFixtures {

    public static Bet.BetBuilder someBet() {
        return Bet.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(new Money(10_00));
    }

}