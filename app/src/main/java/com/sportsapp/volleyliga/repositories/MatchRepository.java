package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;

import java.io.File;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class MatchRepository {

    public static final String URL_BASE = "http://caspermunk.dk";
    public static MatchRepository instance;
    private final MatchApi matchApi;
    private final MatchCache cache;

    public static void setup(File cacheDir) {
        instance = new MatchRepository(cacheDir);
    }

    public MatchRepository(File cacheDir) {
        cache = new MatchCache(cacheDir);
        matchApi = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new MatchRetrofitConverterFactory())
                .build().create(MatchApi.class);
    }

    public Observable<MatchModel> getMatch(int matchNumber) {
        return Observable.concat(Observable.just(cache.retrieve(matchNumber)).filter(matchModel -> matchModel != null), matchApi.getMatch(matchNumber).doOnNext(cache::save)).onErrorResumeNext(throwable -> {
            return Observable.empty();
        });
    }

}
