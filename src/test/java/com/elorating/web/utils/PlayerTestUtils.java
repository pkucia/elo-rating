package com.elorating.web.utils;

import com.elorating.league.LeagueDocument;
import com.elorating.player.PlayerDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pokor on 10.06.2017.
 */
public class PlayerTestUtils {

    public static List<PlayerDocument> generatePlayerList(int size, LeagueDocument league) {
        List<PlayerDocument> players = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            players.add(generatePlayer(String.valueOf(i), league));
        }

        return players;
    }

    public static PlayerDocument generatePlayer(LeagueDocument league) {
        return generatePlayer("1", league);
    }

    public static PlayerDocument generatePlayer(String id, LeagueDocument league)  {
        return new PlayerDocument("PlayerDocument" + id, league);
    }
}
