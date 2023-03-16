package perczynski.kamil.evolution.gameservice.domain;

import perczynski.kamil.evolution.gameservice.libs.Money;

public class PlayerFixtures {

    public static Player.PlayerBuilder somePlayer() {
        return Player.builder()
                .playerId("player-1")
                .freeRoundAvailable(false)
                .balance(new Money(50_00));
    }

}