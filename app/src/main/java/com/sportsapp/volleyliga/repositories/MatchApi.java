package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface MatchApi {

//
//    public static URLFetcher getInstance(String url) {
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(url)
//                .setExecutors(Executors.newFixedThreadPool(5), null)
//                .build();
//        return restAdapter.create(URLFetcher.class);
//    }

    @GET("/livescore/xml/{fileName}.xml")
    Observable<MatchModel> getMatch(@Path("fileName") int matchNumber);
}
