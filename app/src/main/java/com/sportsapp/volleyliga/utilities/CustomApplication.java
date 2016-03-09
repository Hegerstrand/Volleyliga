package com.sportsapp.volleyliga.utilities;


import com.activeandroid.app.Application;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.volley.VolleyQueue;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MatchRepository.setup(getFilesDir());
        VolleyQueue.setup(this);
        TeamRepository.initialize(getFilesDir());
    }
}