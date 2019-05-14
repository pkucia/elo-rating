package com.elorating.league;

import com.elorating.common.AbstractCrudService;
import com.elorating.match.MatchRepository;
import com.elorating.player.PlayerRepository;
import com.elorating.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class LeagueServiceImpl extends AbstractCrudService<League, LeagueRepository> implements LeagueService {

    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository,
                             MatchRepository matchRepository,
                             PlayerRepository playerRepository) {
        super(leagueRepository);
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public void delete(String id)
    {
        matchRepository.deleteByLeagueId(id);
        playerRepository.deleteByLeagueId(id);
        repository.deleteById(id);
    }

    @Override
    public List<League> findByName(String leagueName) {
        return repository.findByNameLikeIgnoreCase(leagueName);
    }

    @Override
    public League update(League league) {
        return repository.findById(league.getId()).map(dbLeague -> {
            dbLeague.setName(league.getName());
            dbLeague.setSettings(league.getSettings());
            repository.save(dbLeague);
            return dbLeague;
        }).orElse(null);
    }

    @Override
    public List<League> findUnassignedLeagues() {
        return repository.findByUsersNull();
    }

    @Override
    public League findByIdAndUser(String leagueId, User user) {
        return repository.findByIdAndUsers(leagueId, user);
    }

    @Override
    public LeagueSettings getSettings(String id) {
        return repository.findById(id)
                .map(League::getSettings)
                .orElse(null);
    }
}
