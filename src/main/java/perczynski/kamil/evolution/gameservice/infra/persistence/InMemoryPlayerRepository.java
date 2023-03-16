package perczynski.kamil.evolution.gameservice.infra.persistence;

import org.springframework.stereotype.Repository;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.libs.ResourceDoesNotExistException;

import java.util.HashMap;
import java.util.Map;

@Repository
@SuppressWarnings("unused")
public class InMemoryPlayerRepository implements PlayerRepository {

    private final Map<String, Player> players = new HashMap<>();

    @Override
    public Player find(String playerId) {
        final Player foundPlayer = players.get(playerId);

        if (foundPlayer == null) {
            throw new ResourceDoesNotExistException(Player.class.getSimpleName(), playerId);
        }
        return foundPlayer;
    }

    @Override
    public Player save(Player player) {
        players.put(player.playerId(), player);
        return player;
    }

}
