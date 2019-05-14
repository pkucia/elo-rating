package com.elorating.league;

import com.elorating.user.User;

import java.util.List;
import java.util.Optional;

public interface LeagueService {

    Optional<League> get(String id);
    List<League> getAll();
    League save(League league);
    List<League> save(Iterable<League> leagues);
    void delete(String id);
    void deleteAll();
    List<League> findByName(String name);
    League findByIdAndUser(String id, User user);
    League update(League league);
    List<League> findUnassignedLeagues();
    LeagueSettings getSettings(String id);
}
