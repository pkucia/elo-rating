package com.elorating.league;

import com.elorating.user.UserDocument;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "league")
public class LeagueDocument {

    @Id
    private String id;

    private String name;

    @DBRef(lazy = true)
    @JsonIgnoreProperties({"leagues", "player"})
    private List<UserDocument> users;

    private Settings settings;

    public LeagueDocument() {
        this.settings = new Settings();
    }

    public LeagueDocument(String id) {
        this();
        this.id = id;
    }

    public LeagueDocument(String id, String name) {
        this(id);
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDocument> getUsers() {
        return users;
    }

    public void addUser(UserDocument user) {
        if (users == null)
            users = new ArrayList<>();
        users.add(user);
    }

    public boolean isAssigned() {
        return users != null && users.size() > 0;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public class Settings {
        private int maxScore;
        private boolean allowDraws;

        public Settings() {
            this.maxScore = 2;
            this.allowDraws = false;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public boolean isAllowDraws() {
            return allowDraws;
        }

        public void setAllowDraws(boolean allowDraws) {
            this.allowDraws = allowDraws;
        }
    }
}
