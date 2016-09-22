package com.volleyapp.volleyliga.utilities;


import com.activeandroid.app.Application;
import com.volleyapp.volleyliga.repositories.LeagueStandingRepository;
import com.volleyapp.volleyliga.repositories.MatchRepository;
import com.volleyapp.volleyliga.repositories.TeamRepository;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MatchRepository.setup(getFilesDir());
        TeamRepository.initialize(getFilesDir());
        LeagueStandingRepository.initialize();
    }
}