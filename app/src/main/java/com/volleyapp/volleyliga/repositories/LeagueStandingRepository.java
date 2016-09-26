package com.volleyapp.volleyliga.repositories;

import com.volleyapp.volleyliga.models.League;
import com.volleyapp.volleyliga.models.LeagueStandingModel;
import com.volleyapp.volleyliga.repositories.xmlParsers.LeagueStandingXmlPullParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;


public class LeagueStandingRepository {
    public static final String URL_BASE = "http://volleyapp.dk";

    public static LeagueStandingRepository instance;
    private final OkHttpClient client;
    private HashMap<League, HashMap<String, LeagueStandingModel>> standings = new HashMap<>();

    public static void initialize() {
        instance = new LeagueStandingRepository();
    }

    public LeagueStandingRepository() {
        client = new OkHttpClient();
        fetch();
    }

    private void fetch() {
        Observable.from(new League[]{League.MALE, League.FEMALE})
                .doOnNext(league -> {
                    Response response = null;
                    try {
                        String path = "";
                        switch (league) {
                            case MALE:
                                path = "volleyliga-herrer";
                                break;
                            case FEMALE:
                                path = "volleyliga-damer";
                                break;
                        }
                        response = client.newCall(new Request.Builder().url(URL_BASE + "/rankings/" + path + ".xml").build()).execute();
                        String result = response.body().string();
                        LeagueStandingXmlPullParser parser = new LeagueStandingXmlPullParser();
                        List<LeagueStandingModel> parsedStandings = parser.parse(result);
                        HashMap<String, LeagueStandingModel> standingMap = new HashMap<>();
                        for (LeagueStandingModel standing : parsedStandings) {
                            standingMap.put(standing.teamName, standing);
                        }
                        standings.put(league, standingMap);
                    } catch (IOException e) {
                    }
                }).subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    public List<LeagueStandingModel> getStandings(League league) {
        HashMap<String, LeagueStandingModel> dict = standings.get(league);
        List<LeagueStandingModel> result = new ArrayList<>(dict.values());
        Collections.sort(result, new LeagueStandingModel.LeagueStandingComparator());
        return result;
    }

    public LeagueStandingModel getStanding(String teamName) {
        for (League league : standings.keySet()) {
            HashMap<String, LeagueStandingModel> temp = standings.get(league);
            if (temp.containsKey(teamName)) {
                return temp.get(teamName);
            }
        }
        return null;
    }
}
