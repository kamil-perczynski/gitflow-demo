package perczynski.kamil.evolution.gameservice.infra.persistence;

import org.springframework.stereotype.Repository;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundRepository;
import perczynski.kamil.evolution.gameservice.libs.ResourceDoesNotExistException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public class InMemoryGameRoundRepository implements GameRoundRepository {

    private final List<GameRound> gameRounds = new LinkedList<>();

    @Override
    public GameRound save(GameRound gameRound) {
        gameRounds.add(gameRound);
        return gameRound;
    }

    @Override
    public GameRound find(String roundId) {
        final Optional<GameRound> foundRound = gameRounds.stream()
                .filter(round -> round.id().equals(roundId))
                .findFirst();

        return foundRound
                .orElseThrow(() -> new ResourceDoesNotExistException(GameRound.class.getSimpleName(), roundId));
    }

    @Override
    public List<GameRound> findByPlayerId(String playerId) {
        return gameRounds.stream()
                .filter(round -> round.playerId().equals(playerId))
                .toList();
    }
}
