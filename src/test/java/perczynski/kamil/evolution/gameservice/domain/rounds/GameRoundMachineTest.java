package perczynski.kamil.evolution.gameservice.domain.rounds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.domain.bets.BetMode;
import perczynski.kamil.evolution.gameservice.libs.Money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRoundMachineTest {

    private GameRoundMachine gameRoundMachine;

    @Mock
    private RandomFacts randomFacts;

    private final GameRoundMachineProperties gameRoundMachineProperties = defaultGameMachineProperties();

    @BeforeEach
    void setup() {
        gameRoundMachine = new GameRoundMachine(
                gameRoundMachineProperties,
                randomFacts
        );
    }

    @Test
    void testFullyLostBet() {
        // given:
        final Bet bet = Bet.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(new Money(100))
                .build();

        when(randomFacts.drawMoneyWin()).thenReturn(false);
        when(randomFacts.drawFreeNextRound()).thenReturn(false);

        // when:
        final Win win = gameRoundMachine.drawWin(bet);

        // then:
        assertThat(win.payout()).isEqualTo(Money.ZERO);
        assertThat(win.freeRound()).isFalse();
    }


    @Test
    void testBigWin() {
        // given:
        final Bet bet = Bet.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(new Money(100))
                .build();

        when(randomFacts.drawFreeNextRound()).thenReturn(false);

        when(randomFacts.drawMoneyWin()).thenReturn(true);
        when(randomFacts.drawBigWin()).thenReturn(true);

        // when:
        final Win win = gameRoundMachine.drawWin(bet);

        // then:
        assertThat(win.payout()).isEqualTo(new Money(5000));
        assertThat(win.freeRound()).isFalse();
    }

    @Test
    void testSmallWin() {
        // given:
        final Bet bet = Bet.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(new Money(100))
                .build();

        when(randomFacts.drawFreeNextRound()).thenReturn(false);
        when(randomFacts.drawMoneyWin()).thenReturn(true);

        // when:
        final Win win = gameRoundMachine.drawWin(bet);

        // then:
        assertThat(win.payout()).isEqualTo(new Money(300));
        assertThat(win.freeRound()).isFalse();
    }

    private static GameRoundMachineProperties defaultGameMachineProperties() {
        return new GameRoundMachineProperties(3, 10, 50);
    }
}