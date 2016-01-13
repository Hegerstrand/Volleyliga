package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.utilities.CustomBus;

import java.util.List;

public interface MatchListActivityView extends MvpView {
    void openMatch(int federationMatchNumber);

    void setData(List<MatchModel> data);
    CustomBus getBus();
}
