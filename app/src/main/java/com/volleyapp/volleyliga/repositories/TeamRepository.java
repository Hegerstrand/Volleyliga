package com.volleyapp.volleyliga.repositories;

import android.content.Context;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.League;
import com.volleyapp.volleyliga.models.TeamModel;
import com.volleyapp.volleyliga.repositories.xmlParsers.TeamXmlPullParser;
import com.volleyapp.volleyliga.utilities.Constants;
import com.volleyapp.volleyliga.utilities.Preferences;

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
//JOLN
    public TeamModel getTeam(String name) {
/*        if(name.equalsIgnoreCase("Team Køge")){
            name = "Team Køge Volley";
        }*/
        if (nameToTeam.containsKey(name.toLowerCase())) {
            return nameToTeam.get(name.toLowerCase());
        }
        return null;
    }

    public League getLeagueForTeam(TeamModel team) {
        if(1100 < team.id && team.id < 1200){
            return League.MALE;
        } else if (1200 < team.id && team.id < 1300){
            return League.FEMALE;
        }
        return League.UNKNOWN;
    }

    public int getTeamMenuItemId(int teamId) {
        switch(teamId){
            case 1101:
                return R.id.asv_aarhus;
            case 1110:
                return R.id.marienlyst;
            case 1114:
                return R.id.gentofte;
            case 1104:
                return R.id.hvidovre;
            case 1105:
                return R.id.ishoj;
            case 1106:
                return R.id.lyngby_gladsaxe;
            case 1107:
                return R.id.middelfart;
            case 1108:
                return R.id.vestsjalland;
            case 1201:
                return R.id.amager;
            case 1202:
                return R.id.brondby;
            case 1203:
                return R.id.ikast;
            case 1204:
                return R.id.eliteVolleyAarhus;
            case 1205:
                return R.id.fortuna;
            case 1206:
                return R.id.holte;
            case 1207:
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
