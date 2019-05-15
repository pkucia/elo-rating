package com.elorating.player;

import com.elorating.common.CrudService;
import com.elorating.match.MatchDocument;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PlayerService extends CrudService<PlayerDocument> {

    List<PlayerDocument> findByLeagueId(String id);
    Long getActivePlayersCountByLeague(String leaugeId);
    List<PlayerDocument> getRanking(String id, Sort sort);
    List<PlayerDocument> findByLeagueIdAndUsername(String leagueId, String username);
    List<PlayerDocument> findActiveByLeagueIdAndUsername(String leagueId, String username);
    void restorePlayers(MatchDocument match);
}
