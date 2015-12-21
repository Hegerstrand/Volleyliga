package com.pocketpalsson.volleyball.utilities;

import android.app.Application;

import com.pocketpalsson.volleyball.repositories.TeamRepository;
import com.pocketpalsson.volleyball.utilities.volley.VolleyQueue;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyQueue.setup(this);
        TeamRepository.initialize();
    }
}