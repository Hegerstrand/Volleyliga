package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.utilities.Constants;
import com.sportsapp.volleyliga.utilities.volley.match.TeamXmlPullParser;

import java.util.HashMap;
import java.util.List;

public class TeamRepository {

    public static TeamRepository instance;
    private HashMap<Integer, TeamModel> idToTeam = new HashMap<>();
    private HashMap<String, TeamModel> nameToTeam = new HashMap<>();

    public static void initialize(){
        instance = new TeamRepository();
    }

    public TeamRepository() {
        TeamXmlPullParser parser = new TeamXmlPullParser();
        List<TeamModel> teams = parser.parse(Constants.TEAM_XML);
        idToTeam = new HashMap<>();
        nameToTeam = new HashMap<>();
        for (TeamModel team : teams) {
            idToTeam.put(team.id, team);
            nameToTeam.put(team.getName().toLowerCase(), team);
        }
    }

    public TeamModel getTeam(int id){
        if(idToTeam.containsKey(id)){
            return idToTeam.get(id);
        }
        return null;
    }

    public TeamModel getTeam(String name){
        if(nameToTeam.containsKey(name.toLowerCase())){
            return nameToTeam.get(name.toLowerCase());
        }
        return null;
    }
}
