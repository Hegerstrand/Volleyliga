package com.sportsapp.volleyliga.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;

@Table(name = "CachedMatches")
public class CachedMatch extends Model{

    @Column(name="matchNumber", index = true)
    public int matchNumber;
    @Column(name="matchType", index = true)
    public int matchType;
    @Column(name="matchJson")
    public String matchJson;
    @Column(name="homeTeam")
    public int homeTeam;
    @Column(name="guestTeam")
    public int guestTeam;

    public MatchModel getMatch(){
        return new Gson().fromJson(matchJson, MatchModel.class);
    }
}
