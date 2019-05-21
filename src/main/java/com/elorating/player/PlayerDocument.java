package com.elorating.player;

import com.elorating.league.LeagueDocument;
import com.elorating.user.UserDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "player")
public class PlayerDocument {

    @Id
    protected String id;
    protected String username;
    protected int rating;
    protected boolean active;
    @DBRef
    @JsonIgnoreProperties({"users", "players"})
    protected LeagueDocument league;
    @DBRef
    @JsonIgnoreProperties({"googleId", "name", "givenName", "familyName", "invitationToken",
                "leagues", "players", "emailsNotifications", "timezone"})
    protected UserDocument user;
    private Statistics statistics;

    public PlayerDocument() {
        this.rating = 1000;
        this.active = true;
        this.statistics = new Statistics();
    }

    public PlayerDocument(String username) {
        this();
        this.username = username;
    }

    public PlayerDocument(String username, LeagueDocument league) {
        this(username);
        this.league = league;
    }

    public PlayerDocument(String username, LeagueDocument league, int rating) {
        this(username, league);
        this.rating = rating;
    }

    public double getExpectedScore(PlayerDocument opponent) {
        return 1 / (1 + Math.pow(10, ((double)(opponent.rating - rating)) / 400));
    }

    @JsonIgnore
    public int getKFactor() {
        if (rating < 2100) {
            return 32;
        } else if (rating >= 2100 && rating <= 2400) {
            return 24;
        } else {
            return 16;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LeagueDocument getLeague() {
        return league;
    }

    public void setLeague(LeagueDocument league) {
        this.league = league;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserDocument getUser() {
        return user;
    }

    public void setUser(UserDocument user) {
        this.user = user;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void updateStatistics(String winnerId) {
        if (winnerId == null)
            statistics.addDraw();
        else if (id.equals(winnerId))
            statistics.addWon();
        else
            statistics.addLost();
        statistics.setLastMatchDate(new Date());
    }

    public void restoreRating(int ratingDelta, boolean isDraw) {
        if (isDraw) {
            int draw = statistics.getDraw();
            statistics.setDraw(--draw);
        } else if (ratingDelta > 0) {
            int won = statistics.getWon();
            statistics.setWon(--won);
        } else if (ratingDelta < 0) {
            int lost = statistics.getLost();
            statistics.setLost(--lost);
        }
        this.rating -= ratingDelta;
    }

    public class Statistics {

        private int won;
        private int lost;
        private int draw;
        private Date lastMatchDate;

        public Statistics() {
            this.won = 0;
            this.lost = 0;
            this.draw = 0;
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
    }
}
