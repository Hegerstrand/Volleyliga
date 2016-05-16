package com.sportsapp.volleyliga.utilities;


import com.activeandroid.app.Application;
import com.sportsapp.volleyliga.repositories.LeagueStandingRepository;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.repositories.TeamRepository;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MatchRepository.setup(getFilesDir());
        TeamRepository.initialize(getFilesDir());
        LeagueStandingRepository.initialize();
    }
}