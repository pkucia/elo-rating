package com.elorating.league;

import com.elorating.common.AbstractCrudService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
class LeagueServiceImpl extends AbstractCrudService<LeagueDocument, LeagueRepository, LeagueModel>
                        implements LeagueService {

    private final Type modelType;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        super(leagueRepository, LeagueDocument.class, LeagueModel.class);
        modelType = new TypeToken<List<LeagueModel>>() {}.getType();
    }

    @Override
    public List<LeagueModel> getAll() {
        List<LeagueDocument> leagueDocuments = repository.findAll();
        return mapper.map(leagueDocuments, modelType);
    }

    @Override
    public void delete(String id) {
        // matchRepository.deleteByLeagueId(id); fixme match service instead
        // playerRepository.deleteByLeagueId(id); fixme player service instead
        repository.deleteById(id);
    }

    @Override
    public List<LeagueModel> findByName(String leagueName) {
        List<LeagueDocument> leagueDocuments = repository.findByNameLikeIgnoreCase(leagueName);
        return mapper.map(leagueDocuments, modelType);
    }

    @Override
    public LeagueModel update(LeagueModel league) {
        return repository.findById(league.getId()).map(leagueDocument -> {
            leagueDocument.setName(league.getName());
            leagueDocument.getSettings().setMaxScore(league.getSettingsMaxScore());
            leagueDocument.getSettings().setAllowDraws(league.isSettingsAllowDraws());
            repository.save(leagueDocument);
            return mapper.map(leagueDocument, LeagueModel.class);
        }).orElse(null);
    }

    @Override
    public List<LeagueModel> findUnassignedLeagues() {
        List<LeagueDocument> leagueDocuments = repository.findByUsersNull();
        return mapper.map(leagueDocuments, modelType);
    }
}
