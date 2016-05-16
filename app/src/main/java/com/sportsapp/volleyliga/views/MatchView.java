package com.sportsapp.volleyliga.views;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;

public interface MatchView extends MvpView {
    Context getActivity();

    void setMatch(MatchModel match);
}
