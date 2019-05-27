package com.elorating.league;

import com.elorating.common.CrudService;

import java.util.List;

public interface LeagueService extends CrudService<LeagueModel> {

    List<LeagueModel> findByName(String name);
    LeagueModel update(LeagueModel league);
    List<LeagueModel> findUnassignedLeagues();
}
