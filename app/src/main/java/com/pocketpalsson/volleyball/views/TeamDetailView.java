package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.pocketpalsson.volleyball.models.TeamModel;

public interface TeamDetailView extends MvpView {
    void setTeamModel(TeamModel team);
}
