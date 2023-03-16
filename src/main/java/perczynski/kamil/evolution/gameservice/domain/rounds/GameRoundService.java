package perczynski.kamil.evolution.gameservice.domain.rounds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoundService {

    private final GameRoundRepository gameRoundRepository;

    public GameRound readGameRound(String roundId) {
        log.info("Reading game round: {}", roundId);
        return gameRoundRepository.find(roundId);
    }
}
