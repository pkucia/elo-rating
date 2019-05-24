package com.elorating.league;

import com.elorating.common.AbstractCrudService;
import com.elorating.match.MatchRepository;
import com.elorating.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class LeagueServiceImpl extends AbstractCrudService<LeagueDocument, LeagueRepository, LeagueModel>
                        implements LeagueService {

    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository,
                             MatchRepository matchRepository,
                             PlayerRepository playerRepository) {
        super(leagueRepository, LeagueDocument.class, LeagueModel.class);
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }


    @Override
    public List<LeagueModel> getAll() {
        // todo
        return null;
    }

    @Override
    public void delete(String id)
    {
        matchRepository.deleteByLeagueId(id);
        playerRepository.deleteByLeagueId(id);
        repository.deleteById(id);
    }

    @Override
    public List<LeagueDocument> findByName(String leagueName) {
        return repository.findByNameLikeIgnoreCase(leagueName);
    }

    @Override
    public LeagueDocument update(LeagueDocument league) {
        return repository.findById(league.getId()).map(dbLeague -> {
            dbLeague.setName(league.getName());
            dbLeague.setSettings(league.getSettings());
            repository.save(dbLeague);
            return dbLeague;
        }).orElse(null);
    }

    @Override
    public List<LeagueDocument> findUnassignedLeagues() {
        return repository.findByUsersNull();
    }

    @Override
    public LeagueDocument.Settings getSettings(String id) {
        return repository.findById(id)
                .map(LeagueDocument::getSettings)
                .orElse(null);
    }
}
