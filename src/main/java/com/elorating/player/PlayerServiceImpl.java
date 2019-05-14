package com.elorating.player;

import com.elorating.match.Match;
import com.elorating.match.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository,
                             MatchRepository matchRepository) {
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<Player> get(String id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    @Override
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public List<Player> save(Iterable<Player> players) {
        return playerRepository.saveAll(players);
    }

    @Override
    public void delete(String id) {
        playerRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        playerRepository.deleteAll();
    }

    @Override
    public List<Player> findByLeagueId(String id) {
        return playerRepository.findByLeagueId(id);
    }

    @Override
    public Long getActivePlayersCountByLeague(String leaugeId) {
        return playerRepository.countByLeagueIdAndActiveIsTrue(leaugeId);
    }

    @Override
    public List<Player> getRanking(String id, Sort sort) {
        return playerRepository.getRanking(id, sort);
    }

    @Override
    public List<Player> findByLeagueIdAndUsername(String leagueId, String username) {
        if (username.length() == 2) {
            String regex = buildInitialsRegex(username);
            System.out.println(regex);
            return playerRepository.findByLeagueIdAndUsernameRegex(leagueId, regex);
        } else if (username.length() > 2) {
            return playerRepository.findByLeagueIdAndUsernameLikeIgnoreCase(leagueId, username);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Player> findActiveByLeagueIdAndUsername(String leagueId, String username) {
        if (username.length() == 2) {
            String regex = buildInitialsRegex(username);
            return playerRepository.findByLeagueIdAndActiveIsTrueAndUsernameRegex(leagueId, regex);
        } else if (username.length() > 2) {
            return playerRepository.findByLeagueIdAndActiveIsTrueAndUsernameLikeIgnoreCase(leagueId, username);
        }
        return new ArrayList<>();
    }

    private String buildInitialsRegex(String username) {
        StringBuilder regex = new StringBuilder("(?i)^");
        String[] split = username.split("");
        regex.append(split[0]).append(".*\\s");
        regex.append(split[1]).append(".*");
        return regex.toString();
    }
    @Override
    public void restorePlayers(Match match) {
        get(match.getPlayerOne().getId()).ifPresent(playerOne -> {
            playerOne.restoreRating(match.getRatingDelta(), match.isDraw());
            Date playerLastMatchDate = getPlayerLastMatchDate(playerOne.getId());
            playerOne.getStatistics().setLastMatchDate(playerLastMatchDate);
            playerRepository.save(playerOne);
        });
        get(match.getPlayerTwo().getId()).ifPresent(playerTwo -> {
            playerTwo.restoreRating(-match.getRatingDelta(), match.isDraw());
            Date playerLastMatchDate = getPlayerLastMatchDate(playerTwo.getId());
            playerTwo.getStatistics().setLastMatchDate(playerLastMatchDate);
            playerRepository.save(playerTwo);
        });
    }

    private Date getPlayerLastMatchDate(String playerId) {
        String dateFieldToSort = "date";
        Sort sort = new Sort(Sort.Direction.DESC, dateFieldToSort);
        PageRequest pageRequest = PageRequest.of(0, 1, sort);
        Page<Match> page = matchRepository.findCompletedByPlayerId(playerId, pageRequest);
        Optional<Date> date = page.stream().findFirst().map(Match::getDate);
        return date.orElse(null);
    }
}
