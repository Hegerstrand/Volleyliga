package com.sportsapp.volleyliga.repositories;

import android.content.Context;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.League;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.repositories.xmlParsers.TeamXmlPullParser;
import com.sportsapp.volleyliga.utilities.Constants;
import com.sportsapp.volleyliga.utilities.Preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;

public class TeamRepository {

    public static TeamRepository instance;
    private HashMap<Integer, TeamModel> idToTeam = new HashMap<>();
    private HashMap<String, TeamModel> nameToTeam = new HashMap<>();
    private File filesDir;

    public static void initialize(File filesDir) {
        instance = new TeamRepository(filesDir);
    }

    public TeamRepository(File filesDir) {
        this.filesDir = filesDir;
        reloadTeams();
    }

    public void saveXml(String teamXml) {
        try {
            File file = new File(filesDir, "teams.xml");

            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(teamXml);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getXml() {
        try {
            File file = new File(filesDir, "teams.xml");
            if (!file.exists()) {
                fetchTeamXml();
                return Constants.TEAM_XML;
            } else {
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }
                br.close();

                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchTeamXml() {
        VolleyBallApi volleyballApi = new Retrofit.Builder()
                .baseUrl(Constants.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(VolleyBallApi.class);
        volleyballApi.getTeamXml().flatMap(response -> {
            try {
                String stringResponse = new String(response.body().bytes());
                return Observable.just(stringResponse);
            } catch (IOException e) {
                return Observable.error(e);
            }
        }).subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        reloadTeams();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String s = "";
                    }

                    @Override
                    public void onNext(String result) {
                        saveXml(result);
                    }
                });
    }

    private void reloadTeams() {
        TeamXmlPullParser parser = new TeamXmlPullParser();
        List<TeamModel> teams = parser.parse(getXml());
        idToTeam = new HashMap<>();
        nameToTeam = new HashMap<>();
        for (TeamModel team : teams) {
            idToTeam.put(team.id, team);
            nameToTeam.put(team.getName().toLowerCase(), team);
        }
    }

    public TeamModel getTeam(int id) {
        if (idToTeam.containsKey(id)) {
            return idToTeam.get(id);
        }
        return null;
    }

    public TeamModel getTeam(String name) {
        if(name.equalsIgnoreCase("Team Køge")){
            name = "Team Køge Volley";
        }
        if (nameToTeam.containsKey(name.toLowerCase())) {
            return nameToTeam.get(name.toLowerCase());
        }
        return null;
    }

    public League getLeagueForTeam(TeamModel team) {
        if(100 < team.id && team.id < 200){
            return League.MALE;
        } else if (200 < team.id && team.id < 300){
            return League.FEMALE;
        }
        return League.UNKNOWN;
    }

    public int getTeamMenuItemId(int teamId) {
        switch(teamId){
            case 101:
                return R.id.asv_aarhus;
            case 102:
                return R.id.marienlyst;
            case 103:
                return R.id.gentofte;
            case 104:
                return R.id.hvidovre;
            case 105:
                return R.id.ishoj;
            case 106:
                return R.id.lyngby_gladsaxe;
            case 107:
                return R.id.middelfart;
            case 108:
                return R.id.vestsjalland;
            case 201:
                return R.id.amager;
            case 202:
                return R.id.brondby;
            case 203:
                return R.id.odense;
            case 204:
                return R.id.eliteVolleyAarhus;
            case 205:
                return R.id.fortuna;
            case 206:
                return R.id.holte;
            case 207:
                return R.id.koge;
        }
        return 0;
    }

    public void setIsFavoriteTeam(Context context, int teamId, boolean isFavorite) {
        Preferences.with(context).setFavoriteTeam(teamId, isFavorite);
        if(isFavorite){

        } else {

        }
    }

    public List<TeamModel> getFavoriteTeams(Context context) {
        HashSet<Integer> favoriteTeamIds = Preferences.with(context).getFavoriteTeams();
        List<TeamModel> result = new ArrayList<>();
        for (Integer id : favoriteTeamIds) {
            TeamModel team = getTeam(id);
            if(team != null){
                result.add(team);
            }
        }
        return result;
    }

    public List<TeamModel> getTeams(League league) {
        List<TeamModel> result = new ArrayList<>();
        int startIndex = 0;
        switch(league){
            case MALE:
                startIndex = 1;
                break;
            case FEMALE:
                startIndex = 9;
                break;
        }

        for (int i = startIndex; i < startIndex + 8; i++) {
            TeamModel team = getTeam(i);
            if(team != null){
                result.add(team);
            }
        }
        return result;
    }

    public boolean isFavorite(int teamId, Context context) {
        return Preferences.with(context).isFavoriteTeam(teamId);
    }
}
