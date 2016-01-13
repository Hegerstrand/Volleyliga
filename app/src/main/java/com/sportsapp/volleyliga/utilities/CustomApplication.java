package com.sportsapp.volleyliga.utilities;

import android.app.Application;

import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.volley.VolleyQueue;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyQueue.setup(this);
        TeamRepository.initialize();
    }
}