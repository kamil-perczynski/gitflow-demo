package perczynski.kamil.evolution.gameservice.domain;

public interface PlayerRepository {

    Player find(String playerId);

    Player save(Player player);

}
