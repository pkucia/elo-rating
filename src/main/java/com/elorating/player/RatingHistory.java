package com.elorating.player;

import com.elorating.match.MatchDocument;

import java.util.Date;

public class RatingHistory {
    private Date date;
    private int rating;
    private String opponent;

    public RatingHistory(MatchDocument match, String playerId) {
        this.date = match.getDate();
        generateRating(match, playerId);
        generateOpponent(match, playerId);
    }

    private void generateRating(MatchDocument match, String playerId) {
        PlayerDocument player = new PlayerDocument();
        player.setId(playerId);
        Integer rating = match.getRating(player);
        this.rating = rating;
    }

    private void generateOpponent(MatchDocument match, String playerId) {
        if (match.getPlayerOne() == null || match.getPlayerTwo() == null)
            this.opponent = "deleted player";
        else if (playerId.equals(match.getPlayerOne().getId()))
            this.opponent = match.getPlayerTwo().getUsername();
        else
            this.opponent = match.getPlayerOne().getUsername();
    }

    public Date getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public String getOpponent() {
        return opponent;
    }
}
