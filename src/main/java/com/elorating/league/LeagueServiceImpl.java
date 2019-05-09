package com.elorating.league;

import com.elorating.model.User;
import com.elorating.repository.MatchRepository;
import com.elorating.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class LeagueServiceImpl implements LeagueService {

    private LeagueRepository leagueRepository;
    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository,
                             MatchRepository matchRepository,
                             PlayerRepository playerRepository) {
        this.leagueRepository = leagueRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public Optional<League> get(String id) {
        return leagueRepository.findById(id);
    }

    @Override
    public List<League> getAll() {
        return leagueRepository.findAll();
    }

    @Override
    public League save(League league) {
        return leagueRepository.save(league);
    }

    @Override
    public List<League> save(Iterable<League> leagues) {
        return leagueRepository.saveAll(leagues);
    }

    @Override
    public void delete(String id)
    {
        matchRepository.deleteByLeagueId(id);
        playerRepository.deleteByLeagueId(id);
        leagueRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        leagueRepository.deleteAll();
    }

    @Override
    public List<League> findByName(String leagueName) {
        return leagueRepository.findByNameLikeIgnoreCase(leagueName);
    }

    @Override
    public League update(League league) {
        return leagueRepository.findById(league.getId()).map(dbLeague -> {
            dbLeague.setName(league.getName());
            dbLeague.setSettings(league.getSettings());
            leagueRepository.save(dbLeague);
            return dbLeague;
        }).orElse(null);
    }

    @Override
    public List<League> findUnassignedLeagues() {
        return leagueRepository.findByUsersNull();
    }

    @Override
    public League findByIdAndUser(String leagueId, User user) {
        return leagueRepository.findByIdAndUsers(leagueId, user);
    }

    @Override
    public LeagueSettings getSettings(String id) {
        return leagueRepository.findById(id)
                .map(League::getSettings)
                .orElse(null);
    }
}
