package com.elorating.match;

import com.elorating.common.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MatchService extends CrudService<MatchDocument> {

    List<MatchDocument> saveAndNotify(List<MatchDocument> matches, String originUrl);
    MatchDocument saveAndNotify(MatchDocument match, String originUrl);
    void deleteByIdWithNotification(String id, String originUrl);
    MatchDocument saveMatchWithPlayers(MatchDocument match);
    List<MatchDocument> findByLeagueId(String leagueId, Sort sortByDate);
    Page<MatchDocument> findByLeagueIdAndCompletedIsTrue(String leagueId, Pageable pageRequest);
    List<MatchDocument> findByLeagueIdAndCompletedIsFalse(String leagueId, Sort sortByDate);
    List<MatchDocument> findByPlayerId(String playerId);
    List<MatchDocument> findByCompletedIsFalse();
    List<MatchDocument> rescheduleMatchesInLeague(String leagueId, int minutes, Sort sort, String originUrl);
    boolean checkIfCompleted(MatchDocument match);
}
