package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.pocketpalsson.volleyball.models.LeagueStandingModel;

import java.util.List;

public interface LeagueStandingView extends MvpView {
    void setData(List<LeagueStandingModel> data);
}
