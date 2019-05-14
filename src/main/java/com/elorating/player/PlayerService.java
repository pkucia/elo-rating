package com.elorating.player;

import com.elorating.match.Match;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    Optional<Player> get(String id);
    List<Player> getAll();
    Player save(Player player);
    List<Player> save(Iterable<Player> players);
    void delete(String id);
    void deleteAll();
    List<Player> findByLeagueId(String id);
    Long getActivePlayersCountByLeague(String leaugeId);
    List<Player> getRanking(String id, Sort sort);
    List<Player> findByLeagueIdAndUsername(String leagueId, String username);
    List<Player> findActiveByLeagueIdAndUsername(String leagueId, String username);
    void restorePlayers(Match match);
}
