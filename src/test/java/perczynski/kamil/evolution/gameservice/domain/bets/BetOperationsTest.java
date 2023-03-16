package perczynski.kamil.evolution.gameservice.domain.bets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.libs.ErrorCodeException;
import perczynski.kamil.evolution.gameservice.libs.Money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static perczynski.kamil.evolution.gameservice.domain.GameErrorCode.INSUFFICIENT_FUNDS;
import static perczynski.kamil.evolution.gameservice.domain.PlayerFixtures.somePlayer;
import static perczynski.kamil.evolution.gameservice.domain.bets.BetFixtures.someBet;
import static perczynski.kamil.evolution.gameservice.libs.MockAnswers.withFirstParameter;

@ExtendWith(MockitoExtension.class)
class BetOperationsTest {

    @InjectMocks
    private BetOperations betOperations;

    @Mock
    private PlayerRepository playerRepository;

    @Test
    void testPlaceBet() {
        // given:
        final Player player = somePlayer()
                .balance(new Money(50_00))
                .build();
        final Bet bet = someBet()
                .stake(new Money(10_00))
                .build();

        when(playerRepository.find(any())).thenReturn(player);
        when(playerRepository.save(any())).thenAnswer(withFirstParameter());

        // when:
        betOperations.tryPlaceBet(player.playerId(), bet);

        // then:
        final Player updatedPlayer = captureUpdatedPlayer(playerRepository);
        assertThat(updatedPlayer.balance()).isEqualTo(new Money(40_00));
    }

    @Test
    void testPlaceBetToPlayForFree() {
        // given:
        final Player player = somePlayer().build();
        final Bet bet = someBet()
                .mode(BetMode.PLAY_FOR_FREE)
                .build();

        when(playerRepository.find(any())).thenReturn(player);
        when(playerRepository.save(any())).thenAnswer(withFirstParameter());

        // when:
        betOperations.tryPlaceBet(player.playerId(), bet);

        // then:
        final Player updatedPlayer = captureUpdatedPlayer(playerRepository);
        assertThat(updatedPlayer.balance()).isEqualTo(player.balance());
    }

    @Test
    void testPlaceFreeBet() {
        // given:
        final Player player = somePlayer()
                .freeRoundAvailable(true)
                .build();
        final Bet bet = someBet()
                .mode(BetMode.PLAY_FOR_CASH)
                .build();

        when(playerRepository.find(any())).thenReturn(player);
        when(playerRepository.save(any())).thenAnswer(withFirstParameter());

        // when:
        betOperations.tryPlaceBet(player.playerId(), bet);

        // then:
        final Player updatedPlayer = captureUpdatedPlayer(playerRepository);
        assertThat(updatedPlayer.balance()).isEqualTo(player.balance());
    }

    @Test
    void testPlaceBetWithInsufficientFunds() {
        // given:
        final Player player = somePlayer()
                .balance(new Money(5_00))
                .build();
        final Bet bet = someBet()
                .stake(new Money(7_00))
                .build();

        when(playerRepository.find(any())).thenReturn(player);

        // when:
        final ErrorCodeException exception = catchThrowableOfType(
                () -> betOperations.tryPlaceBet(player.playerId(), bet),
                ErrorCodeException.class
        );

        // then:
        assertThat(exception.getCode()).isEqualTo(INSUFFICIENT_FUNDS);
    }

    private static Player captureUpdatedPlayer(PlayerRepository playerRepository) {
        final ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());
        return captor.getValue();
    }

}