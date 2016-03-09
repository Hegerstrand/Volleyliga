package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.Constants;

import java.io.File;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    public Observable<MatchModel> getMatchesFromCache(MatchModel.Type matchType) {
        return cache.getMatchesByType(matchType)
                .filter(matchModel -> matchModel != null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MatchModel> updatePastMatches() {
        return volleyBallApi.updatePastMatches()
                .doOnNext(match -> cache.save(match, MatchModel.Type.PAST))
                .onErrorResumeNext(throwable -> {
                    return Observable.empty();
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
