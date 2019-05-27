package com.elorating.league;

import com.elorating.user.UserModel;

import java.util.List;

public class LeagueModel {

    private String id;
    private String name;
    private List<UserModel> users;
    private int settingsMaxScore;
    private boolean settingsAllowDraws;

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

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public int getSettingsMaxScore() {
        return settingsMaxScore;
    }

    public void setSettingsMaxScore(int settingsMaxScore) {
        this.settingsMaxScore = settingsMaxScore;
    }

    public boolean isSettingsAllowDraws() {
        return settingsAllowDraws;
    }

    public void setSettingsAllowDraws(boolean settingsAllowDraws) {
        this.settingsAllowDraws = settingsAllowDraws;
    }
}
