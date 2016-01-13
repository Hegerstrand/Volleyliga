package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.TeamModel;

public interface TeamDetailView extends MvpView {
    void setTeamModel(TeamModel team);
}
