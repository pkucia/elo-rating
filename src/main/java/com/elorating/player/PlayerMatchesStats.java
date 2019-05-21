package com.elorating.player;

import java.util.Date;

public class PlayerMatchesStats {

    private int won;
    private int lost;
    private int draw;
    private Date lastMatchDate;
    private int setsWon;
    private int setsLost;
    private int maxRating;
    private int minRating;
    private Date maxRatingDate;
    private Date minRatingDate;

    public PlayerMatchesStats() {
        this.won = 0;
        this.lost = 0;
        this.draw = 0;
        this.setsWon = 0;
        this.setsLost = 0;
        this.maxRating = 1000;
        this.minRating = 1000;
        this.maxRatingDate = new Date();
        this.minRatingDate = new Date();
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

    public void addWon() {
        won++;
    }

    public void addLost() {
        lost++;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void addDraw() {
        draw++;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public Date getLastMatchDate() {
        return lastMatchDate;
    }

    public void setLastMatchDate(Date lastMatchDate) {
        this.lastMatchDate = lastMatchDate;
    }

    public int getSetsWon() {
        return setsWon;
    }

    public void addSetsWon(int sets) {
        this.setsWon += sets;
    }

    public int getSetsLost() {
        return setsLost;
    }

    public void addSetsLost(int sets) {
        this.setsLost += sets;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }

    public int getMinRating() {
        return minRating;
    }

    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }

    public Date getMaxRatingDate() {
        return maxRatingDate;
    }

    public void setMaxRatingDate(Date maxRatingDate) {
        this.maxRatingDate = maxRatingDate;
    }

    public Date getMinRatingDate() {
        return minRatingDate;
    }

    public void setMinRatingDate(Date minRatingDate) {
        this.minRatingDate = minRatingDate;
    }
}
