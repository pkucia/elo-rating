package com.elorating.player;

import com.elorating.match.MatchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

public interface PlayerMatchesService {

    List<MatchDocument> findByPlayerId(String playerId, Sort sort);
    List<MatchDocument> findScheduledByPlayerId(String playerId, Sort sort);
    Page<MatchDocument> findCompletedByPlayerId(String playerId, PageRequest pageRequest);
    List<MatchDocument> findCompletedByPlayerId(String playerId, Sort sort);
    List<MatchDocument> findCompletedByPlayerIds(String playerId, String opponentId, Sort sort);
    Page<MatchDocument> findCompletedByPlayerIds(String playerId, String opponentId, PageRequest pageRequest);
    List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Sort sort);
    List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Date to, Sort sort);
    List<MatchDocument> getMatchForecast(String playerId, String opponentId);
}
