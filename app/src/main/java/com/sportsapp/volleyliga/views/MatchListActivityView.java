package com.sportsapp.volleyliga.views;

import android.app.Activity;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;

import java.util.List;

public interface MatchListActivityView extends MvpView {
    void openMatch(int federationMatchNumber);

    void addMatches(List<MatchModel> data);

    void loadFinished();

    Activity getActivity();
}
