package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.CustomBus;

public interface MatchView extends MvpView {
    void setMatchModel(MatchModel match);

    CustomBus getBus();
}
