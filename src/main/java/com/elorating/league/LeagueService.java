package com.elorating.league;

import com.elorating.common.CrudService;
import com.elorating.user.UserDocument;

import java.util.List;

public interface LeagueService extends CrudService<LeagueDocument> {

    List<LeagueDocument> findByName(String name);
    LeagueDocument findByIdAndUser(String id, UserDocument user);
    LeagueDocument update(LeagueDocument league);
    List<LeagueDocument> findUnassignedLeagues();
    LeagueSettings getSettings(String id);
}
