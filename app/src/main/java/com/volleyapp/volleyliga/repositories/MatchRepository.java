package com.volleyapp.volleyliga.repositories;

import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.repositories.xmlParsers.MatchCollectionXmlPullParser;
import com.volleyapp.volleyliga.utilities.Constants;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MatchRepository {
    public static final String URL_BASE = "http://volleyapp.dk";

    public static MatchRepository instance;
    private final VolleyBallApi volleyBallApi;
    private final MatchCache cache;

    public static void setup(File cacheDir) {
        instance = new MatchRepository(cacheDir);
    }

    public MatchRepository(File cacheDir) {
        cache = new MatchCache(cacheDir);
        volleyBallApi = new Retrofit.Builder()
                .baseUrl(Constants.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new MatchRetrofitConverterFactory())
                .addConverterFactory(new MatchCollectionRetrofitConverterFactory())
                .build().create(VolleyBallApi.class);
    }

    public Observable<MatchModel> getMatch(int matchNumber) {
        return Observable.concat(
                Observable.just(cache.retrieve(matchNumber))
                        .filter(matchModel -> matchModel != null),
                volleyBallApi.getMatch(matchNumber)
                        .doOnNext(cache::save)
        ).onErrorResumeNext(throwable -> {
            return Observable.empty();
        });
    }

    public Observable<MatchModel> getAllMatches() {
//        return getMatchesOfType(MatchModel.Type.PAST);
        return Observable.merge(getMatchesOfType(MatchModel.Type.TODAY), getMatchesOfType(MatchModel.Type.PAST), getMatchesOfType(MatchModel.Type.FUTURE));
    }

    public Observable<MatchModel> getMatchesOfType(MatchModel.Type matchType) {
        return Observable.concat(getMatchesFromCache(matchType), getMatchesFromWeb(matchType))
                .onErrorResumeNext(throwable -> {
                    return Observable.empty();
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MatchModel> getMatchesFromWeb(MatchModel.Type matchType) {
        Observable<MatchCollection> source = Observable.empty();
        switch (matchType) {
            case PAST:
                source = volleyBallApi.getPastMatches();
                break;
            case TODAY:
                source = volleyBallApi.getTodayMatches();
                break;
            case FUTURE:
                source = volleyBallApi.getFutureMatches();
                break;
        }
        return source.flatMap(matchCollection -> Observable.from(matchCollection.matches)).doOnNext(match -> cache.save(match, matchType));
    }

    public Observable<MatchModel.Type> updateAllMatches() {
        return Observable.from(new MatchModel.Type[]{MatchModel.Type.FUTURE, MatchModel.Type.TODAY, MatchModel.Type.PAST})
                .doOnNext(matchType -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        String filename = "";
                        switch (matchType) {
                            case FUTURE:
                                filename = "/future";
                                break;
                            case TODAY:
                                filename = "/today";
                                break;
                            case PAST:
                                filename = "/past";
                                break;
                        }
                        Response response = client.newCall(new Request.Builder().url(URL_BASE + filename + ".xml").build()).execute();
                        String result = response.body().string();
                        MatchCollectionXmlPullParser parser = new MatchCollectionXmlPullParser();
                        MatchCollection parsedCollection = parser.parse(result);
                        for (MatchModel match : parsedCollection.matches) {
                            cache.save(match, matchType);
                        }
                    } catch (IOException | XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }).subscribeOn(Schedulers.newThread());
    }

    public Observable<MatchModel> getMatchesFromCache(MatchModel.Type matchType) {
        return cache.getMatchesByType(matchType)
                .filter(matchModel -> matchModel != null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MatchModel> getMatchesForTeamFromCache(int teamId) {
        return cache.getMatchesForTeam(teamId)
                .filter(matchModel -> matchModel != null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public List<MatchModel> getFutureMatchesForTeam(int teamId) {
        return cache.getFutureMatchesForTeam(teamId);
    }

    public MatchModel getMostRecentMatchBetweenTeams(int teamId1, int teamId2, Date comparisonDate) {
        return cache.getMostRecentMatchBetweenTeams(teamId1, teamId2, comparisonDate);
    }
}
