package com.sportsapp.volleyliga.views;

import android.content.res.Resources;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface MainActivityView extends MvpView {
    void openMatch(int federationMatchNumber);

    Resources getResources();
}
