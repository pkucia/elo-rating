package com.elorating.player;

import com.elorating.common.AbstractCrudService;
import com.elorating.match.MatchDocument;
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
class PlayerServiceImpl extends AbstractCrudService<PlayerDocument, PlayerRepository> implements PlayerService {

    private final MatchRepository matchRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository,
                             MatchRepository matchRepository) {
        super(playerRepository);
        this.matchRepository = matchRepository;
    }

    @Override
    public List<PlayerDocument> findByLeagueId(String id) {
        return repository.findByLeagueId(id);
    }

    @Override
    public Long getActivePlayersCountByLeague(String leaugeId) {
        return repository.countByLeagueIdAndActiveIsTrue(leaugeId);
    }

    @Override
    public List<PlayerDocument> getRanking(String id, Sort sort) {
        return repository.getRanking(id, sort);
    }

    @Override
    public List<PlayerDocument> findByLeagueIdAndUsername(String leagueId, String username) {
        if (username.length() == 2) {
            String regex = buildInitialsRegex(username);
            System.out.println(regex);
            return repository.findByLeagueIdAndUsernameRegex(leagueId, regex);
        } else if (username.length() > 2) {
            return repository.findByLeagueIdAndUsernameLikeIgnoreCase(leagueId, username);
        }
        return new ArrayList<>();
    }

    @Override
    public List<PlayerDocument> findActiveByLeagueIdAndUsername(String leagueId, String username) {
        if (username.length() == 2) {
            String regex = buildInitialsRegex(username);
            return repository.findByLeagueIdAndActiveIsTrueAndUsernameRegex(leagueId, regex);
        } else if (username.length() > 2) {
            return repository.findByLeagueIdAndActiveIsTrueAndUsernameLikeIgnoreCase(leagueId, username);
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
    public void restorePlayers(MatchDocument match) {
        get(match.getPlayerOne().getId()).ifPresent(playerOne -> {
            playerOne.restoreRating(match.getRatingDelta(), match.isDraw());
            Date playerLastMatchDate = getPlayerLastMatchDate(playerOne.getId());
            playerOne.getStatistics().setLastMatchDate(playerLastMatchDate);
            repository.save(playerOne);
        });
        get(match.getPlayerTwo().getId()).ifPresent(playerTwo -> {
            playerTwo.restoreRating(-match.getRatingDelta(), match.isDraw());
            Date playerLastMatchDate = getPlayerLastMatchDate(playerTwo.getId());
            playerTwo.getStatistics().setLastMatchDate(playerLastMatchDate);
            repository.save(playerTwo);
        });
    }

    private Date getPlayerLastMatchDate(String playerId) {
        String dateFieldToSort = "date";
        Sort sort = new Sort(Sort.Direction.DESC, dateFieldToSort);
        PageRequest pageRequest = PageRequest.of(0, 1, sort);
        Page<MatchDocument> page = matchRepository.findCompletedByPlayerId(playerId, pageRequest);
        Optional<Date> date = page.stream().findFirst().map(MatchDocument::getDate);
        return date.orElse(null);
    }
}
