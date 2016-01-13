package com.sportsapp.volleyliga.utilities;

import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public class SimpleURLFetcher {

    public static URLFetcher getInstance(String url) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .setExecutors(Executors.newFixedThreadPool(5), null)
                .build();
        return restAdapter.create(URLFetcher.class);
    }

    public interface URLFetcher {
        @GET("/{fileName}")
        Observable<Response> getResponse(@Path("fileName") String fileName);
    }
}
