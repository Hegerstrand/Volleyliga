package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;
import com.squareup.okhttp.ResponseBody;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface VolleyBallApi {

//
//    public static URLFetcher getInstance(String url) {
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(url)
//                .setExecutors(Executors.newFixedThreadPool(5), null)
//                .build();
//        return restAdapter.create(URLFetcher.class);
//    }

    @GET("/xml/{fileName}.xml")
    Observable<MatchModel> getMatch(@Path("fileName") int matchNumber);

    @GET("/collections/past.xml")
    Observable<MatchModel> updatePastMatches();

    @GET("/collections/today.xml")
    Observable<MatchModel> updateTodayMatches();

    @GET("/collections/future.xml")
    Observable<MatchModel> updateFutureMatches();

    @GET("/teamsandplayers.xml")
    Observable<Response<ResponseBody>> getTeamXml();
}
