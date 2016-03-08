package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.utilities.Constants;
import com.sportsapp.volleyliga.utilities.volley.match.TeamXmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
            File file = new File(filesDir, "Teams.xml");

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
            File file = new File(filesDir, "Teams.xml");
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
        if (nameToTeam.containsKey(name.toLowerCase())) {
            return nameToTeam.get(name.toLowerCase());
        }
        return null;
    }
}
