package com.elorating.player;

import com.elorating.common.CrudService;
import com.elorating.match.Match;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PlayerService extends CrudService<Player> {

    List<Player> findByLeagueId(String id);
    Long getActivePlayersCountByLeague(String leaugeId);
    List<Player> getRanking(String id, Sort sort);
    List<Player> findByLeagueIdAndUsername(String leagueId, String username);
    List<Player> findActiveByLeagueIdAndUsername(String leagueId, String username);
    void restorePlayers(Match match);
}
