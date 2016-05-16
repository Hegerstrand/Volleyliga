package com.sportsapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;

import java.util.List;

public interface TeamDetailView extends MvpView {
    void setTeamModel(TeamModel team);
    void setData(List<MatchModel> data);
}
