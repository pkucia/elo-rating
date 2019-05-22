package com.elorating.match;

import com.elorating.league.LeagueDocument;
import com.elorating.player.PlayerDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "match")
public class MatchDocument {

    @Id
    private String id;

    @DBRef
    @JsonIgnoreProperties({"users", "settings"})
    private LeagueDocument league;

    @DBRef
    @JsonIgnoreProperties({"league"})
    private PlayerDocument playerOne;

    @DBRef
    @JsonIgnoreProperties({"league"})
    private PlayerDocument playerTwo;

    private Map<String, Integer> scores;

    private Map<String, Integer> ratings;

    private Date date;

    private boolean completed;

    private int ratingDelta;

    public MatchDocument() {
        this.date = new Date();
        this.scores = new HashMap<>();
        this.ratings = new HashMap<>();
        this.completed = false;
    }

    public MatchDocument(PlayerDocument playerOne, PlayerDocument playerTwo) {
        this();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public MatchDocument(PlayerDocument playerOne, PlayerDocument playerTwo, LeagueDocument league) {
        this(playerOne, playerTwo);
        this.league = league;
    }

    public MatchDocument(PlayerDocument playerOne, PlayerDocument playerTwo, int playerOneScore, int playerTwoScore) {
        this(playerOne, playerTwo);
        this.scores.put(playerOne.getId(), playerOneScore);
        this.scores.put(playerTwo.getId(), playerTwoScore);
        this.setCompleted();
    }

    public MatchDocument(PlayerDocument playerOne, PlayerDocument playerTwo, int playerOneScore, int playerTwoScore, Date date) {
        this(playerOne, playerTwo, playerOneScore, playerTwoScore);
        this.date = date;
    }

    public MatchDocument(PlayerDocument playerOne, PlayerDocument playerTwo, int playerOneScore, int playerTwoScore, LeagueDocument league) {
        this(playerOne, playerTwo, playerOneScore, playerTwoScore);
        this.league = league;
    }

    public boolean isCompleted() {
        return scores.size() == 2;
    }

    public PlayerDocument getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(PlayerDocument playerOne) {
        this.playerOne = playerOne;
    }

    public PlayerDocument getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(PlayerDocument playerTwo) {
        this.playerTwo = playerTwo;
    }

    public LeagueDocument getLeague() {
        return league;
    }

    public void setLeague(LeagueDocument league) {
        this.league = league;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public int getScore(PlayerDocument player) {
        return scores.get(player.getId());
    }

    public void setScore(PlayerDocument player, Integer score) {
        scores.put(player.getId(), score);
    }

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public int getRating(PlayerDocument player) {
        return ratings.get(player.getId()) != null ? ratings.get(player.getId()) : 0;
    }

    public void setRating(PlayerDocument player, Integer rating) {
        ratings.put(player.getId(), rating);
    }

    public int getRatingDelta() {
        return ratingDelta;
    }

    public void setRatingDelta(int ratingDelta) {
        this.ratingDelta = ratingDelta;
    }

    public int getRatingDelta(PlayerDocument player) {
        if (getPlayerOne().getId().equals(player.getId())) {
            return ratingDelta;
        } else {
            return -ratingDelta;
        }
    }

    public void updateRatingsWithDelta(int delta) {
        ratings.put(playerOne.getId(), playerOne.getRating() + delta);
        ratings.put(playerTwo.getId(), playerTwo.getRating() - delta);
        this.ratingDelta = delta;
    }

    public void removePlayerId(String playerId) {
        if (playerOne != null && playerOne.getId().equals(playerId))
            playerOne = null;
        else if (playerTwo != null && playerTwo.getId().equals(playerId))
            playerTwo = null;

        removePlayerScore(playerId);
    }

    private void removePlayerScore(String playerId) {
        Integer score = scores.remove(playerId);
        scores.put("", score);
    }

    public void setCompleted() {
        this.completed = isCompleted();
    }

    @JsonIgnore
    public String getWinnerId() {
        if (isDraw())
            return null;
        else
            return Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    @JsonIgnore
    public String getLooserId() {
        if (isDraw())
            return null;
        else
            return Collections.min(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    @JsonIgnore
    public boolean isDraw() {
        if (scores != null && scores.size() > 0) {
            Set<Integer> values = new HashSet<>(scores.values());
            return values.size() == 1;
        }
        return true;
    }
}
