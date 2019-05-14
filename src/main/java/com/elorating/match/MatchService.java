package com.elorating.match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface MatchService {

    Optional<Match> get(String id);
    List<Match> getAll();
    Match save(Match match);
    List<Match> save(Iterable<Match> matches);
    void delete(String id);
    void deleteAll();
    List<Match> saveAndNotify(List<Match> matches, String originUrl);
    Match saveAndNotify(Match match, String originUrl);
    void deleteByIdWithNotification(String id, String originUrl);
    Match saveMatchWithPlayers(Match match);
    List<Match> findByLeagueId(String leagueId, Sort sortByDate);
    Page<Match> findByLeagueIdAndCompletedIsTrue(String leagueId, Pageable pageRequest);
    List<Match> findByLeagueIdAndCompletedIsFalse(String leagueId, Sort sortByDate);
    List<Match> findByPlayerId(String playerId);
    List<Match> findByCompletedIsFalse();
    List<Match> rescheduleMatchesInLeague(String leagueId, int minutes, Sort sort, String originUrl);
    boolean checkIfCompleted(Match match);
}
