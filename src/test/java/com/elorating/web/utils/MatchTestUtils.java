package com.elorating.web.utils;

import com.elorating.league.LeagueDocument;
import com.elorating.match.MatchDocument;
import com.elorating.player.PlayerDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pokor on 10.06.2017.
 */
public class MatchTestUtils {

    public static MatchDocument generateMatch(LeagueDocument league, PlayerDocument winner, PlayerDocument loser, boolean completed) {
        MatchDocument match = new MatchDocument(winner, loser, league);
        match.setScore(winner, 2);
        match.setScore(loser, 0);
        if (completed) {
            match.setCompleted();
        }

        return match;
    }

    public static List<MatchDocument> setupMatches(PlayerDocument playerOne, PlayerDocument playerTwo, LeagueDocument league, int amount) {
        List<MatchDocument> matchList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            MatchDocument match = new MatchDocument(playerOne, playerTwo, league);
            matchList.add(match);
        }

        return matchList;
    }

}
