package perczynski.kamil.evolution.gameservice.domain.rounds;

import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;

import java.util.List;

public interface GameRoundRepository {

    GameRound save(GameRound gameRound);

    List<GameRound> findByPlayerId(String playerId);

    GameRound find(String roundId);

}
