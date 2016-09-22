package com.volleyapp.volleyliga.models;

public class SetInfoModel {
    public int scoreHome, scoreGuest, minutesPlayed;
    public boolean homeWon;

    public SetInfoModel(int scoreHome, int scoreGuest, int minutesPlayed) {
        this.scoreHome = scoreHome;
        this.scoreGuest = scoreGuest;
        this.minutesPlayed = minutesPlayed;
        homeWon = scoreHome > scoreGuest;
    }

    public String getText() {
        return scoreHome + " - " + scoreGuest;
    }
}
