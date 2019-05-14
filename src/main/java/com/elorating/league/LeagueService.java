package com.elorating.league;

import com.elorating.common.CrudService;
import com.elorating.user.User;

import java.util.List;

public interface LeagueService extends CrudService<League> {

    List<League> findByName(String name);
    League findByIdAndUser(String id, User user);
    League update(League league);
    List<League> findUnassignedLeagues();
    LeagueSettings getSettings(String id);
}
