package perczynski.kamil.evolution.gameservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.domain.bets.BetMode;
import perczynski.kamil.evolution.gameservice.domain.bets.BetOperations;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundMachine;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundRepository;
import perczynski.kamil.evolution.gameservice.domain.rounds.Win;
import perczynski.kamil.evolution.gameservice.libs.ErrorCodeException;
import perczynski.kamil.evolution.gameservice.libs.Money;

import static perczynski.kamil.evolution.gameservice.domain.GameErrorCode.INVALID_BET_AMOUNT;
import static perczynski.kamil.evolution.gameservice.libs.Uuids.nextUuid;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private static final Money DEFAULT_INITIAL_BALANCE = new Money(5000_00);

    private final PlayerRepository playerRepository;
    private final GameRoundRepository gameRoundRepository;
    private final GameRoundMachine gameRoundMachine;
    private final BetOperations betOperations;

    public Player registerPlayer() {
        final String playerId = nextUuid();
        log.info("Registering new player: {}", playerId);

        return playerRepository.save(
                Player.builder()
                        .balance(DEFAULT_INITIAL_BALANCE)
                        .freeRoundAvailable(false)
                        .playerId(playerId)
                        .build()
        );
    }

    public GameRound placeBet(String playerId, Bet bet) {
        checkBetAmount(bet);
        log.info("Placing bet: {} for player: {}", bet, playerId);
        betOperations.tryPlaceBet(playerId, bet);

        final Win win = gameRoundMachine.drawWin(bet);
        final Player nextPlayer = applyWins(playerId, bet, win);

        return gameRoundRepository.save(
                toBetOutcome(playerId, bet, win, nextPlayer)
        );
    }

    private synchronized Player applyWins(String playerId, Bet bet, Win win) {
        final Player mostRecentPlayer = playerRepository.find(playerId);
        if (bet.mode() == BetMode.PLAY_FOR_FREE) {
            return mostRecentPlayer;
        }

        final Money nextBalance = mostRecentPlayer.balance().plus(win.payout());

        final Player nextPlayer = mostRecentPlayer.toBuilder()
                .freeRoundAvailable(win.freeRound())
                .balance(nextBalance)
                .build();
        return playerRepository.save(nextPlayer);
    }


    private static GameRound toBetOutcome(String playerId,
                                          Bet bet,
                                          Win win,
                                          Player nextPlayer) {
        return GameRound.builder()
                .id(nextUuid())
                .playerId(playerId)
                .bet(bet)
                .win(win)
                .nextBalance(nextPlayer.balance())
                .build();
    }

    private static void checkBetAmount(Bet bet) {
        if (bet.stake().amount() < 1_00 || bet.stake().amount() > 10_00) {
            throw new ErrorCodeException(INVALID_BET_AMOUNT);
        }
    }
}
