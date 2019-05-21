package com.elorating.player;

import com.elorating.match.MatchDocument;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel("PlayerDocument's opponent statistics")
public class OpponentStats {

    private String id;
    private int won;
    private int lost;
    private int draw;
    private PlayerDocument player;
    private PlayerDocument opponent;
    private int pointsGained;
    private int streak;

    public OpponentStats(PlayerDocument player, PlayerDocument opponent) {
        this.won = 0;
        this.lost = 0;
        this.draw = 0;
        this.player = player;
        this.opponent = opponent;
    }

    public void setStats(List<MatchDocument> matches) {
        streak = 0;
        boolean stopStreak = false;
        for (MatchDocument match : matches) {
            if (match.isDraw()) {
                draw++;
                stopStreak = true;
            } else if (this.player.getId().equals(match.getWinnerId())) {
                won++;
                if (!stopStreak) {
                    if (streak >= 0) streak++;
                    else stopStreak = true;
                }
            } else {
                lost++;
                if (!stopStreak) {
                    if (streak <= 0) streak--;
                    else stopStreak = true;
                }
            }
            pointsGained += match.getRatingDelta(player);
        }
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getDraw() {
        return draw;
    }

    public int getPointsGained() {
        return pointsGained;
    }

    public void setPointsGained(int pointsGained) {
        this.pointsGained = pointsGained;
    }

    public PlayerDocument getPlayer() {
        return player;
    }

    public PlayerDocument getOpponent() {
        return opponent;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getTotal() {
        return this.won + this.lost;
    }
}
