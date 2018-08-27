package com.elorating.service;

import com.elorating.model.Match;
import com.elorating.model.OpponentStats;
import com.elorating.model.Player;
import com.elorating.model.RatingHistory;
import com.elorating.repository.MatchRepository;
import com.elorating.repository.PlayerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pokor on 10.06.2017.
 */
@Service
public class PlayerStatsService {

    @Resource
    private PlayerRepository playerRepository;

    @Resource
    private MatchRepository matchRepository;

    public OpponentStats getOpponentStats(String playerId, String opponentId) {
        Sort sortByDate = new Sort(Sort.Direction.DESC, "date");
        Player player = playerRepository.findOne(playerId);
        Player opponent = playerRepository.findOne(opponentId);
        List<Match> matches = matchRepository.findCompletedByPlayerIds(playerId, opponentId, sortByDate);
        OpponentStats opponentStats = new OpponentStats(player, opponent);
        opponentStats.setStats(matches);
        return opponentStats;
    }

    public List<OpponentStats> getOpponentsStats(String playerId) {
        Player player = playerRepository.findOne(playerId);
        List<Player> opponents = playerRepository.findByIdNotAndLeagueId(playerId, player.getLeague().getId());
        ArrayList<OpponentStats> opponentStats = new ArrayList<>(opponents.size());
        for (Player opponent : opponents) {
            opponentStats.add(getOpponentStats(playerId, opponent.getId()));
        }
        return opponentStats;
    }

    public List<RatingHistory> getRatingHistory(String playerId, Date from, Date to, Sort sort) {
        List<Match> matches = matchRepository.findCompletedByPlayerIdAndDate(playerId, from, to, sort);
        return buildRatingHistory(matches, playerId);
    }

    public List<RatingHistory> getRatingHistory(String playerId, Date from, Sort sort) {
        List<Match> matches = matchRepository.findCompletedByPlayerIdAndDate(playerId, from, sort);
        return buildRatingHistory(matches, playerId);
    }

    public List<RatingHistory> getRatingHistory(String playerId, Sort sort) {
        List<Match> matches = matchRepository.findCompletedByPlayerId(playerId, sort);
        return buildRatingHistory(matches, playerId);
    }

    private List<RatingHistory> buildRatingHistory(List<Match> matches, String playerId) {
        List<RatingHistory> history = new ArrayList<>();
        for (Match match : matches) {
            history.add(new RatingHistory(match, playerId));
        }
        return history;
    }
}
