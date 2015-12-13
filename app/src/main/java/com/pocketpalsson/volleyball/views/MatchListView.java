package com.pocketpalsson.volleyball.views;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.pocketpalsson.volleyball.models.MatchModel;

import java.util.List;

public interface MatchListView extends MvpView {
    MainActivityView getActivityView();

    void setData(List<MatchModel> data);
}
