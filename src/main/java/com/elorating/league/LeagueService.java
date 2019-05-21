package com.elorating.league;

import com.elorating.common.CrudService;

import java.util.List;

public interface LeagueService extends CrudService<LeagueDocument> {

    List<LeagueDocument> findByName(String name);
    LeagueDocument update(LeagueDocument league);
    List<LeagueDocument> findUnassignedLeagues();
    LeagueDocument.Settings getSettings(String id);
}
