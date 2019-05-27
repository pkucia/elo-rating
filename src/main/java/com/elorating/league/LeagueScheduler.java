package com.elorating.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeagueScheduler {

    private final LeagueService leagueService;

    @Autowired
    public LeagueScheduler(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @Scheduled(cron = "0 5 23 * * *")
    public void removeUnassignedLeagues() {
        List<LeagueModel> leaguesToRemove = leagueService.findUnassignedLeagues();
        for (LeagueModel league : leaguesToRemove) {
            String leagueId = league.getId();
            leagueService.delete(leagueId);
        }
    }
}
