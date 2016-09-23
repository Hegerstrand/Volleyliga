package com.volleyapp.volleyliga.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.models.TeamModel;

import java.util.List;

public interface TeamDetailView extends MvpView {
    void setTeamModel(TeamModel team);
    void setData(List<MatchModel> data);
}
