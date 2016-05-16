package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.LeagueStandingModel;

import java.util.List;

public interface ILeagueStandingView extends MvpView {
    void setData(List<LeagueStandingModel> data);
}
