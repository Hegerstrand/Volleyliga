package com.sportsapp.volleyliga.models;

public class LeagueStandingModel {
    public TeamModel team;
    public int playedGames, wonGames, lostGames, points, position;

    public LeagueStandingModel(TeamModel team, int playedGames, int wonGames, int lostGames, int points, int position) {
        this.team = team;
        this.playedGames = playedGames;
        this.wonGames = wonGames;
        this.lostGames = lostGames;
        this.points = points;
        this.position = position;
    }
}
