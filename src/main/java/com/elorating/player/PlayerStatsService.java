package com.elorating.player;

import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

public interface PlayerStatsService {

    OpponentStats getOpponentStats(String playerId, String opponentId);
    List<OpponentStats> getOpponentsStats(String playerId);
    List<RatingHistory> getRatingHistory(String playerId, Date from, Date to, Sort sort);
    List<RatingHistory> getRatingHistory(String playerId, Date from, Sort sort);
    List<RatingHistory> getRatingHistory(String playerId, Sort sort);
    PlayerMatchesStats getPlayerMatchesStats(String playerId);
}
