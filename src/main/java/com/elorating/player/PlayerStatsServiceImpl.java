package com.elorating.player;

import com.elorating.match.MatchDocument;
import com.elorating.match.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
class PlayerStatsServiceImpl implements PlayerStatsService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public PlayerStatsServiceImpl(PlayerRepository playerRepository,
                                  MatchRepository matchRepository) {
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public OpponentStats getOpponentStats(String playerId, String opponentId) {
        Sort sortByDate = new Sort(Sort.Direction.DESC, "date");
        Optional<PlayerDocument> player = playerRepository.findById(playerId);
        Optional<PlayerDocument> opponent = playerRepository.findById(opponentId);
        if (player.isPresent() && opponent.isPresent()) {
            List<MatchDocument> matches = matchRepository.findCompletedByPlayerIds(playerId, opponentId, sortByDate);
            OpponentStats opponentStats = new OpponentStats(player.get(), opponent.get());
            opponentStats.setStats(matches);
            return opponentStats;
        }
        return null;
    }

    @Override
    public List<OpponentStats> getOpponentsStats(String playerId) {
        return playerRepository.findById(playerId).map(player -> {
            List<PlayerDocument> opponents = playerRepository.findByIdNotAndLeagueId(playerId, player.getLeague().getId());
            ArrayList<OpponentStats> opponentStats = new ArrayList<>(opponents.size());
            for (PlayerDocument opponent : opponents) {
                opponentStats.add(getOpponentStats(playerId, opponent.getId()));
            }
            return opponentStats;
        }).orElseGet(ArrayList::new);
    }

    @Override
    public List<RatingHistory> getRatingHistory(String playerId, Date from, Date to, Sort sort) {
        List<MatchDocument> matches = matchRepository.findCompletedByPlayerIdAndDate(playerId, from, to, sort);
        return buildRatingHistory(matches, playerId);
    }

    @Override
    public List<RatingHistory> getRatingHistory(String playerId, Date from, Sort sort) {
        List<MatchDocument> matches = matchRepository.findCompletedByPlayerIdAndDate(playerId, from, sort);
        return buildRatingHistory(matches, playerId);
    }

    @Override
    public List<RatingHistory> getRatingHistory(String playerId, Sort sort) {
        List<MatchDocument> matches = matchRepository.findCompletedByPlayerId(playerId, sort);
        return buildRatingHistory(matches, playerId);
    }

    private List<RatingHistory> buildRatingHistory(List<MatchDocument> matches, String playerId) {
        List<RatingHistory> history = new ArrayList<>();
        for (MatchDocument match : matches) {
            history.add(new RatingHistory(match, playerId));
        }
        return history;
    }

    @Override
    public PlayerMatchesStats getPlayerMatchesStats(String playerId) {
        PlayerMatchesStats statistics = new PlayerMatchesStats();
        List<MatchDocument> matches = matchRepository.findCompletedByPlayerId(playerId);
        for (MatchDocument match : matches) {
            getMatchRatio(playerId, statistics, match);
            getSetsRatio(playerId, statistics, match);
            setMinMaxRating(playerId, statistics, match);
        }
        return statistics;
    }

    private void getMatchRatio(String playerId, PlayerMatchesStats statistics, MatchDocument match) {
        if (match.isDraw())
            statistics.addDraw();
        else if (playerId.equals(match.getWinnerId()))
            statistics.addWon();
        else
            statistics.addLost();
    }

    private void getSetsRatio(String playerId, PlayerMatchesStats statistics, MatchDocument match) {
        for (String key : match.getScores().keySet()) {
            int sets = match.getScores().get(key);
            if (playerId.equals(key))
                statistics.addSetsWon(sets);
            else
                statistics.addSetsLost(sets);
        }
    }

    private void setMinMaxRating(String playerId, PlayerMatchesStats statistics, MatchDocument match) {
        PlayerDocument player = new PlayerDocument();
        player.setId(playerId);
        int rating = match.getRating(player);
        if (rating > statistics.getMaxRating()) {
            statistics.setMaxRating(rating);
            statistics.setMaxRatingDate(match.getDate());
        }
        if (rating < statistics.getMinRating()) {
            statistics.setMinRating(rating);
            statistics.setMinRatingDate(match.getDate());
        }
    }
}
