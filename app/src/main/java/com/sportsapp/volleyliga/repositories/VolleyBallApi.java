package com.sportsapp.volleyliga.repositories;

import com.sportsapp.volleyliga.models.MatchModel;
import com.squareup.okhttp.ResponseBody;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface VolleyBallApi {

    @GET("/xml/{fileName}.xml")
    Observable<MatchModel> getMatch(@Path("fileName") int matchNumber);

    @GET("/collections/past.xml")
    Observable<MatchCollection> getPastMatches();

    @GET("/collections/today.xml")
    Observable<MatchCollection> getTodayMatches();

    @GET("/collections/future.xml")
    Observable<MatchCollection> getFutureMatches();

    @GET("/teamsandplayers.xml")
    Observable<Response<ResponseBody>> getTeamXml();
}
