package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.Constants;

import java.io.File;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class MatchRepository {

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
                .build().create(VolleyBallApi.class);
    }

    public Observable<MatchModel> getMatch(int matchNumber) {
        return Observable.concat(Observable.just(cache.retrieve(matchNumber)).filter(matchModel -> matchModel != null), volleyBallApi.getMatch(matchNumber).doOnNext(cache::save)).onErrorResumeNext(throwable -> {
            return Observable.empty();
        });
    }

}
